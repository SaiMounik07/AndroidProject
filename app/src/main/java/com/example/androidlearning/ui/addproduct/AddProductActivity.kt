package com.example.androidlearning.ui.addproduct

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
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.androidlearning.R
import com.example.androidlearning.databinding.ActivityAddProductBinding
import com.example.androidlearning.data.model.Badge
import com.example.androidlearning.data.model.Price
import com.example.androidlearning.data.model.Product
import com.example.androidlearning.data.model.Review
import com.example.androidlearning.ui.search.SearchActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import java.io.File

class AddProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddProductBinding
    private lateinit var viewModel: AddProductViewModel
    private var selectedImageUrl: String? = null
    private var capturedImageUri: Uri? = null
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUrl = uri.toString()
                displayImage(uri)
                Toast.makeText(this, "Image selected from gallery", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, "Photo captured", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(this, "Camera permission denied. Using placeholder instead.", Toast.LENGTH_LONG).show()
            usePlaceholderImage()
        }
    }

    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openGallery()
        } else {
            Toast.makeText(this, "Storage permission denied. Using placeholder instead.", Toast.LENGTH_LONG).show()
            usePlaceholderImage()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[AddProductViewModel::class.java]
        with(binding) {
            binding.etListPrice.addTextChangedListener {
                viewModel.getDiscountPrice(it.toString().toInt(), etSalePrice.text.toString().toInt())
            }
            viewModel.discountPercent.observe( this@AddProductActivity) { value ->
                etDiscountPercent.setText("$value%")
            }
        }



        setupClickListeners()

    }

    private fun setupClickListeners() {
        with(binding) {
            cardImage.setOnClickListener {
                showImageSourceDialog()
            }

            btnCancel.setOnClickListener {
                finish()
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
        
        AlertDialog.Builder(this)
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
                this,
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
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                openGallery()
            }
            else -> {
                storagePermissionLauncher.launch(permission)
            }
        }
    }

    private fun openCamera() {
        try {
            val photoFile = File(cacheDir, "product_${System.currentTimeMillis()}.jpg")
            capturedImageUri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                photoFile
            )
            
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri)
            cameraLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Camera not available: ${e.message}", Toast.LENGTH_SHORT).show()
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
        Toast.makeText(this, "Placeholder image added", Toast.LENGTH_SHORT).show()
    }

    private fun displayImage(imageSource: Any) {
        binding.ivAddPlaceholder.visibility = android.view.View.GONE
        binding.tvAddImageText.visibility = android.view.View.GONE
        binding.ivProductPreview.visibility = android.view.View.VISIBLE
        
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
            val salePrice = etSalePrice.text.toString().toDouble().toInt()
            val listPrice = etListPrice.text.toString().toDoubleOrNull()?.toInt() ?: salePrice
            val location = etLocation.text.toString().ifBlank { "Unknown" }
            viewModel.getDiscountPrice(listPrice,salePrice)

            val discount = if (listPrice > salePrice) {
                ((listPrice - salePrice).toDouble() / listPrice * 100).toInt()
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

    private fun formatPrice(price: Int): String {
        return price.toString().reversed().chunked(3).joinToString(".").reversed()
    }

    private fun saveProduct(product: Product) {
        viewModel.saveProduct(product)
        Snackbar.make(binding.root,"Product saved: ${product.name}",Snackbar.LENGTH_LONG).setAction("UNDO"){
            viewModel.deleteProduct(product)
        }.show()
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, SearchActivity::class.java))
        }, 1000)    }
}
