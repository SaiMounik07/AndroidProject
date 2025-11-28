package com.example.androidlearning.ui.addproduct.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.androidlearning.R
import com.example.androidlearning.data.model.Badge
import com.example.androidlearning.data.model.Price
import com.example.androidlearning.data.model.Product
import com.example.androidlearning.data.model.Review
import com.example.androidlearning.databinding.ActivityAddProductBinding
import com.example.androidlearning.ui.addproduct.AddProductViewModel
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.text.NumberFormat
import java.util.Locale

class AddProductFragment : Fragment() {
    private var _binding: ActivityAddProductBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AddProductViewModel
    private var selectedImageUrl: String? = null
    private var capturedImageUri: Uri? = null

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUrl = uri.toString()
                displayImage(uri)
                Toast.makeText(requireContext(), "Image selected from gallery", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            capturedImageUri?.let { uri ->
                selectedImageUrl = uri.toString()
                displayImage(uri)
                Toast.makeText(requireContext(), "Photo captured", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(requireContext(), "Camera permission denied. Using placeholder instead.", Toast.LENGTH_LONG).show()
            usePlaceholderImage()
        }
    }

    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openGallery()
        } else {
            Toast.makeText(requireContext(), "Storage permission denied. Using placeholder instead.", Toast.LENGTH_LONG).show()
            usePlaceholderImage()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityAddProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[AddProductViewModel::class.java]

        with(binding) {
            etListPrice.addTextChangedListener {
                val listPriceText = it.toString()
                val salePriceText = etSalePrice.text.toString()
                if (listPriceText.isNotEmpty() && salePriceText.isNotEmpty()) {
                    viewModel.getDiscountPrice(listPriceText.toDouble(), salePriceText.toDouble())
                }
            }
            viewModel.discountPercent.observe(viewLifecycleOwner) { value ->
                etDiscountPercent.setText("${value.toInt()} %")
            }
        }

        // Check if in edit mode and pre-fill data
        arguments?.let { args ->
            if (args.getBoolean("EDIT_MODE", false)) {
                preFillProductData(args)
                binding.tvHeader.text = "Edit Product"
                binding.btnSave.text = "Update Product"
            }
        }

        setupClickListeners()
    }

    private fun preFillProductData(args: Bundle) {
        with(binding) {
            etProductName.setText(args.getString("PRODUCT_NAME") ?: "")
            etBrand.setText(args.getString("PRODUCT_BRAND") ?: "")
            etSalePrice.setText(args.getDouble("PRODUCT_SALE_PRICE", 0.0).toString())

            val listPrice = args.getDouble("PRODUCT_LIST_PRICE", 0.0)
            if (listPrice > 0.0) {
                etListPrice.setText(listPrice.toString())
            }

            etLocation.setText(args.getString("PRODUCT_LOCATION") ?: "")

            cbFreeShipping.isChecked = args.getBoolean("PRODUCT_FREE_SHIPPING", false)
            cbFreeGift.isChecked = args.getBoolean("PRODUCT_FREE_GIFT", false)
            cbFlashSale.isChecked = args.getBoolean("PRODUCT_FLASH_SALE", false)
            cbOfficialStore.isChecked = args.getBoolean("PRODUCT_OFFICIAL_STORE", false)
            cbDiamondStore.isChecked = args.getBoolean("PRODUCT_DIAMOND_STORE", false)

            val imageUrl = args.getString("PRODUCT_IMAGE")
            if (!imageUrl.isNullOrEmpty()) {
                selectedImageUrl = imageUrl
                displayImage(imageUrl)
            }
        }
    }

    private fun setupClickListeners() {
        with(binding) {
            cardImage.setOnClickListener {
                showImageSourceDialog()
            }

            btnCancel.setOnClickListener {
                parentFragmentManager.popBackStack()
            }

            btnSave.setOnClickListener {
                if (validateInputs()) {
                    val product = createProductFromInputs()
                    saveProduct(product)
                }
            }
        }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Use Placeholder")

        AlertDialog.Builder(requireContext())
            .setTitle("Add Product Image")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermissionAndOpen()
                    1 -> checkStoragePermissionAndOpen()
                    2 -> usePlaceholderImage()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun checkCameraPermissionAndOpen() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun checkStoragePermissionAndOpen() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED -> {
                openGallery()
            }
            else -> {
                storagePermissionLauncher.launch(permission)
            }
        }
    }

    private fun openCamera() {
        try {
            val photoFile =
                File(requireContext().cacheDir, "product_${System.currentTimeMillis()}.jpg")
            capturedImageUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                photoFile
            )

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri)
            cameraLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Camera not available: ${e.message}", Toast.LENGTH_SHORT).show()
            usePlaceholderImage()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        galleryLauncher.launch(intent)
    }

    private fun usePlaceholderImage() {
        val placeholderUrls = listOf(
            "https://www.static-src.com/wcsstore/Indraprastha/images/catalog/medium/catalog-image/MTA-0406056/enzim_enzim_orthodontic_colostrum_enhanced_pasta_gigi_-124_gr-_full32_bkdk84uh.jpeg",
            "https://www.static-src.com/wcsstore/Indraprastha/images/catalog/medium//90/MTA-2163267/hamsfood_makanan-hamster---hamster-food-hd-300-gram_full02.jpg",
            "https://www.static-src.com/wcsstore/Indraprastha/images/catalog/medium/catalog-image/MTA-15958002/b29_groceries_-_b29_power_water_solution_detergent_bubuk_-777_g-_x_2_pcs_full01_txjzn8y3.jpg",
            "https://www.static-src.com/wcsstore/Indraprastha/images/catalog/medium/catalog-image/114/MTA-176963586/tidak_ada_merk_tas_jaring_belanja_jumbo_penyimpan_sayur_buah_bawang_groceries_bag_kantong_multifungsi_full01_kpf6ianv.jpg"
        )

        selectedImageUrl = placeholderUrls.random()
        displayImage(selectedImageUrl!!)
        Toast.makeText(requireContext(), "Placeholder image added", Toast.LENGTH_SHORT).show()
    }

    private fun displayImage(imageSource: Any) {
        binding.ivAddPlaceholder.visibility = View.GONE
        binding.tvAddImageText.visibility = View.GONE
        binding.ivProductPreview.visibility = View.VISIBLE

        Glide.with(this)
            .load(imageSource)
            .placeholder(R.drawable.img_1)
            .error(R.drawable.img_1)
            .into(binding.ivProductPreview)
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        with(binding) {
            if (etProductName.text.isNullOrBlank()) {
                tilProductName.error = "Product name is required"
                isValid = false
            } else {
                tilProductName.error = null
            }
            if (etSalePrice.text.isNullOrBlank()) {
                tilSalePrice.error = "Sale price is required"
                isValid = false
            } else {
                try {
                    etSalePrice.text.toString().toDouble()
                    tilSalePrice.error = null
                } catch (e: NumberFormatException) {
                    tilSalePrice.error = "Invalid price format"
                    isValid = false
                }
            }
            if (selectedImageUrl == null) {
                selectedImageUrl = "https://www.static-src.com/wcsstore/Indraprastha/images/catalog/medium/catalog-image/MTA-0406056/enzim_enzim_orthodontic_colostrum_enhanced_pasta_gigi_-124_gr-_full32_bkdk84uh.jpeg"
            }

            if (!etListPrice.text.isNullOrBlank()) {
                try {
                    val listPrice = etListPrice.text.toString().toDouble()
                    val salePrice = etSalePrice.text.toString().toDoubleOrNull() ?: 0.0
                    if (listPrice < salePrice) {
                        tilListPrice.error = "Original price must be greater than sale price"
                        isValid = false
                    } else {
                        tilListPrice.error = null
                    }
                } catch (e: NumberFormatException) {
                    tilListPrice.error = "Invalid price format"
                    isValid = false
                }
            }
        }

        return isValid
    }

    private fun createProductFromInputs(): Product {
        with(binding) {
            val name = etProductName.text.toString()
            val brand = etBrand.text.toString().ifBlank { "no brand" }
            val salePrice = etSalePrice.text.toString().toDouble()
            val listPrice = etListPrice.text.toString().toDoubleOrNull() ?: salePrice
            val location = etLocation.text.toString().ifBlank { "Unknown" }
            viewModel.getDiscountPrice(listPrice, salePrice)

            val discount = if (listPrice > salePrice) {
                ((listPrice - salePrice) / listPrice * 100).toInt()
            } else {
                0
            }

            val discountPrice = if (discount > 0) (listPrice - salePrice).toDouble() else 0.0

            val price = Price(
                priceDisplay = "Rp${formatPrice(salePrice)}",
                strikeThroughPriceDisplay = if (discount > 0) "Rp${formatPrice(listPrice)}" else "",
                discount = discount,
                discountPrice = discountPrice,
                minPrice = salePrice.toDouble(),
                offerPriceDisplay = "Rp${formatPrice(salePrice)}",
                isPriceRange = false,
                listPrice = listPrice,
                salePrice = salePrice
            )

            val tags = mutableListOf<String>()
            if (cbFreeShipping.isChecked) tags.add("FREE_SHIPPING")
            if (cbFreeGift.isChecked) tags.add("FREE_GIFT")
            if (cbFlashSale.isChecked) tags.add("FLASH_SALE_CAMPAIGN")

            val finalBrand = if (cbOfficialStore.isChecked && brand == "no brand") {
                etProductName.text.toString().split(" ").firstOrNull() ?: "no brand"
            } else {
                brand
            }

            val review = Review(
                rating = 0,
                count = 0,
                absoluteRating = 0.0,
                sellerRating = 0.0,
                isNewSeller = true
            )

            val merchantBadge = when {
                cbDiamondStore.isChecked -> "Diamond"
                cbOfficialStore.isChecked -> "Gold"
                else -> "None"
            }

            val merchantBadgeUrl = when (merchantBadge) {
                "Diamond" -> "https://www.static-src.com//siva/asset/06_2025/seller-diamond.png"
                "Gold" -> "https://www.static-src.com//siva/asset/06_2025/seller-gold.png"
                else -> ""
            }

            val badge = Badge(
                logisticBadge_stock = if (cbFreeShipping.isChecked) "2HD" else "",
                merchantBadgeUrl = merchantBadgeUrl,
                merchantBadge = merchantBadge,
                logisticBadge = if (cbFreeShipping.isChecked) "2HD" else ""
            )

            return Product(
                name = name,
                price = price,
                brand = finalBrand,
                review = review,
                tags = tags,
                location = location,
                badge = badge,
                soldCountTotal = 0,
                uspLabelsTags = tags,
                images = listOf(selectedImageUrl ?: "")
            )
        }
    }

    private fun formatPrice(price: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale("in", "ID"))
        return formatter.format(price.toInt())
    }

    private fun saveProduct(product: Product) {
        val isEditMode = arguments?.getBoolean("EDIT_MODE", false) ?: false

        if (isEditMode) {
            val oldProductName = arguments?.getString("PRODUCT_NAME") ?: ""
            if (oldProductName.isNotEmpty()) {
                val oldProduct = createProductFromOldData(oldProductName)
                viewModel.deleteProduct(oldProduct)
            }
        }

        viewModel.saveProduct(product)

        val message = if (isEditMode) "Product updated: ${product.name}" else "Product saved: ${product.name}"
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).setAction("UNDO") {
            viewModel.deleteProduct(product)
            if (isEditMode) {
                val oldProductName = arguments?.getString("PRODUCT_NAME") ?: ""
                if (oldProductName.isNotEmpty()) {
                    val oldProduct = createProductFromOldData(oldProductName)
                    viewModel.saveProduct(oldProduct)
                }
            }
        }.show()

        Handler(Looper.getMainLooper()).postDelayed({
            parentFragmentManager.popBackStack()
        }, 1000)
    }

    private fun createProductFromOldData(oldName: String): Product {
        val args = arguments ?: return Product(
            name = oldName,
            price = Price("", "", 0, 0.0, 0.0, "", false, 0.0, 0.0),
            brand = "no brand",
            review = Review(0, 0, 0.0, 0.0, true),
            tags = emptyList(),
            location = "",
            badge = Badge("", "", "", ""),
            soldCountTotal = 0,
            uspLabelsTags = emptyList(),
            images = emptyList()
        )

        val salePrice = args.getDouble("PRODUCT_SALE_PRICE", 0.0)
        val listPrice = args.getDouble("PRODUCT_LIST_PRICE", 0.0)
        val brand = args.getString("PRODUCT_BRAND") ?: "no brand"
        val location = args.getString("PRODUCT_LOCATION") ?: "Unknown"
        val imageUrl = args.getString("PRODUCT_IMAGE") ?: ""

        val discount = if (listPrice > salePrice) {
            ((listPrice - salePrice) / listPrice * 100).toInt()
        } else {
            0
        }

        val price = Price(
            priceDisplay = "Rp${formatPrice(salePrice)}",
            strikeThroughPriceDisplay = if (discount > 0) "Rp${formatPrice(listPrice)}" else "",
            discount = discount,
            discountPrice = if (discount > 0) (listPrice - salePrice) else 0.0,
            minPrice = salePrice,
            offerPriceDisplay = "Rp${formatPrice(salePrice)}",
            isPriceRange = false,
            listPrice = listPrice,
            salePrice = salePrice
        )

        val tags = mutableListOf<String>()
        if (args.getBoolean("PRODUCT_FREE_SHIPPING", false)) tags.add("FREE_SHIPPING")
        if (args.getBoolean("PRODUCT_FREE_GIFT", false)) tags.add("FREE_GIFT")
        if (args.getBoolean("PRODUCT_FLASH_SALE", false)) tags.add("FLASH_SALE_CAMPAIGN")

        val merchantBadge = when {
            args.getBoolean("PRODUCT_DIAMOND_STORE", false) -> "Diamond"
            args.getBoolean("PRODUCT_OFFICIAL_STORE", false) -> "Gold"
            else -> "None"
        }

        val merchantBadgeUrl = when (merchantBadge) {
            "Diamond" -> "https://www.static-src.com//siva/asset/06_2025/seller-diamond.png"
            "Gold" -> "https://www.static-src.com//siva/asset/06_2025/seller-gold.png"
            else -> ""
        }

        val badge = Badge(
            logisticBadge_stock = if (tags.contains("FREE_SHIPPING")) "2HD" else "",
            merchantBadgeUrl = merchantBadgeUrl,
            merchantBadge = merchantBadge,
            logisticBadge = if (tags.contains("FREE_SHIPPING")) "2HD" else ""
        )

        return Product(
            name = oldName,
            price = price,
            brand = brand,
            review = Review(0, 0, 0.0, 0.0, true),
            tags = tags,
            location = location,
            badge = badge,
            soldCountTotal = 0,
            uspLabelsTags = tags,
            images = if (imageUrl.isNotEmpty()) listOf(imageUrl) else emptyList()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}