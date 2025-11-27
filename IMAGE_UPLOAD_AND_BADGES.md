# 📸 Image Upload & Store Badges - Enhanced Features

## Overview

I've enhanced the Add Product feature with:
1. ✅ **Real Image Upload** from device gallery
2. ✅ **Official Store** badge
3. ✅ **Diamond Store** badge  
4. ✅ **Flash Sale** tag

---

## 📸 Image Upload Implementation

### How It Works:

**1. Image Picker Launcher:**
```kotlin
private val imagePickerLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
) { result ->
    if (result.resultCode == Activity.RESULT_OK) {
        result.data?.data?.let { uri ->
            selectedImageUri = uri
            selectedImageUrl = uri.toString()
            
            // Display selected image using Glide
            Glide.with(this)
                .load(uri)
                .placeholder(R.drawable.img_1)
                .into(binding.ivProductPreview)
            
            Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show()
        }
    }
}
```

**2. Open Image Picker:**
```kotlin
private fun openImagePicker() {
    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    intent.type = "image/*"
    imagePickerLauncher.launch(intent)
}
```

**3. Trigger on FAB Click:**
```kotlin
fabAddImage.setOnClickListener {
    openImagePicker()
}
```

### User Flow:

```
1. User clicks camera FAB button
   ↓
2. System image picker opens
   ↓
3. User selects image from gallery
   ↓
4. Image URI captured
   ↓
5. Image displayed in preview using Glide
   ↓
6. Image URI saved to product
```

---

## 🏪 Store Badges

### Official Store Checkbox

**UI:**
```xml
<CheckBox
    android:id="@+id/cb_official_store"
    android:text="Official Store" />
```

**Logic:**
```kotlin
// If official store is checked and no brand provided,
// use first word of product name as brand
val finalBrand = if (cbOfficialStore.isChecked && brand == "no brand") {
    etProductName.text.toString().split(" ").firstOrNull() ?: "no brand"
} else {
    brand
}
```

**Badge Creation:**
```kotlin
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
```

**Effect:**
- Shows official store icon in product card
- Sets brand automatically if not provided
- Adds Gold badge

---

### Diamond Store Checkbox

**UI:**
```xml
<CheckBox
    android:id="@+id/cb_diamond_store"
    android:text="Diamond Store" />
```

**Logic:**
- Takes priority over Official Store
- Sets merchant badge to "Diamond"
- Shows diamond icon in product card

**Badge Hierarchy:**
```
Diamond Store (highest)
    ↓
Official Store (Gold)
    ↓
None (default)
```

---

## ⚡ Flash Sale Tag

**UI:**
```xml
<CheckBox
    android:id="@+id/cb_flash_sale"
    android:text="Flash Sale" />
```

**Logic:**
```kotlin
val tags = mutableListOf<String>()
if (cbFreeShipping.isChecked) tags.add("FREE_SHIPPING")
if (cbFreeGift.isChecked) tags.add("FREE_GIFT")
if (cbFlashSale.isChecked) tags.add("FLASH_SALE_CAMPAIGN")
```

**Effect:**
- Adds "FLASH_SALE_CAMPAIGN" to tags
- Shows flash sale icon in product card
- Indicates time-limited offer

---

## 🔐 Permissions

### Added to AndroidManifest.xml:

```xml
<!-- For Android 12 and below -->
<uses-permission 
    android:name="android.permission.READ_EXTERNAL_STORAGE" 
    android:maxSdkVersion="32" />

<!-- For Android 13+ -->
<uses-permission 
    android:name="android.permission.READ_MEDIA_IMAGES" />
```

**Why Two Permissions?**
- Android 13+ uses granular media permissions
- `READ_MEDIA_IMAGES` for Android 13+
- `READ_EXTERNAL_STORAGE` for older versions
- `maxSdkVersion="32"` ensures proper permission handling

---

## 🎨 Complete Badge System

### Badge Object Creation:

```kotlin
val badge = Badge(
    logisticBadge_stock = if (cbFreeShipping.isChecked) "2HD" else "",
    merchantBadgeUrl = merchantBadgeUrl,
    merchantBadge = merchantBadge,
    logisticBadge = if (cbFreeShipping.isChecked) "2HD" else ""
)
```

### Badge Components:

| Field | Value | Condition |
|-------|-------|-----------|
| `merchantBadge` | "Diamond" | Diamond Store checked |
| `merchantBadge` | "Gold" | Official Store checked |
| `merchantBadge` | "None" | Neither checked |
| `merchantBadgeUrl` | Diamond PNG | Diamond Store |
| `merchantBadgeUrl` | Gold PNG | Official Store |
| `logisticBadge` | "2HD" | Free Shipping checked |
| `logisticBadge_stock` | "2HD" | Free Shipping checked |

---

## 📱 Updated UI Layout

### Store Information Section:

```
┌─────────────────────────────────┐
│  Store Information (Optional)   │
├─────────────────────────────────┤
│  ☐ Official Store  ☐ Diamond   │
└─────────────────────────────────┘
```

### Product Tags Section:

```
┌─────────────────────────────────┐
│  Product Tags (Optional)        │
├─────────────────────────────────┤
│  ☐ Free Shipping  ☐ Free Gift  │
│  ☐ Flash Sale                   │
└─────────────────────────────────┘
```

---

## 🔄 Complete Product Creation Flow

### Step-by-Step:

```kotlin
1. User fills product name: "Samsung Galaxy S24"
   ↓
2. User checks "Official Store"
   ↓
3. Brand auto-set to "Samsung" (first word)
   ↓
4. User checks "Diamond Store"
   ↓
5. Badge set to "Diamond" (overrides Gold)
   ↓
6. User checks "Flash Sale"
   ↓
7. Tag "FLASH_SALE_CAMPAIGN" added
   ↓
8. User checks "Free Shipping"
   ↓
9. Tag "FREE_SHIPPING" added
10. Logistic badge set to "2HD"
   ↓
11. User clicks camera FAB
   ↓
12. Selects image from gallery
   ↓
13. Image displayed in preview
   ↓
14. User clicks "Save Product"
   ↓
15. Product created with all badges and tags
```

---

## 🎯 Product Display Examples

### Example 1: Diamond Store with Flash Sale

**Input:**
- Name: "iPhone 15 Pro Max"
- Official Store: ✅
- Diamond Store: ✅
- Flash Sale: ✅
- Free Shipping: ✅

**Result:**
```
Product {
    name = "iPhone 15 Pro Max"
    brand = "iPhone"
    badge = Badge(
        merchantBadge = "Diamond",
        merchantBadgeUrl = "...seller-diamond.png",
        logisticBadge = "2HD"
    )
    tags = ["FREE_SHIPPING", "FLASH_SALE_CAMPAIGN"]
}
```

**Display:**
```
┌─────────────────────────┐
│ [Image]  ⚡ 🚚          │
│ iPhone 15 Pro Max       │
│ Rp15.000.000           │
│ 💎 iPhone Official     │
│ [Lihat Produk]         │
└─────────────────────────┘
```

---

### Example 2: Official Store Only

**Input:**
- Name: "Nike Air Max"
- Official Store: ✅
- Diamond Store: ❌
- Flash Sale: ❌
- Free Gift: ✅

**Result:**
```
Product {
    name = "Nike Air Max"
    brand = "Nike"
    badge = Badge(
        merchantBadge = "Gold",
        merchantBadgeUrl = "...seller-gold.png"
    )
    tags = ["FREE_GIFT"]
}
```

**Display:**
```
┌─────────────────────────┐
│ [Image]  🎁            │
│ Nike Air Max           │
│ Rp2.500.000           │
│ ✓ Nike Official        │
│ [Lihat Produk]         │
└─────────────────────────┘
```

---

## 🔧 Image Handling

### Using Glide for Image Loading:

**Advantages:**
- ✅ Efficient memory management
- ✅ Automatic caching
- ✅ Placeholder support
- ✅ Error handling
- ✅ Smooth loading

**Implementation:**
```kotlin
Glide.with(this)
    .load(uri)                      // Load from URI
    .placeholder(R.drawable.img_1)  // Show while loading
    .error(R.drawable.img_1)        // Show if error
    .into(binding.ivProductPreview) // Target ImageView
```

---

## 📊 Complete Field Summary

| Field | Type | Required | Default | Notes |
|-------|------|----------|---------|-------|
| Product Name | Text | ✅ Yes | - | - |
| Sale Price | Number | ✅ Yes | - | - |
| Product Image | Image | ✅ Yes | - | From gallery |
| Brand | Text | ❌ No | "no brand" | Auto-set if official store |
| Original Price | Number | ❌ No | Same as sale | For discounts |
| Location | Text | ❌ No | "Unknown" | - |
| Official Store | Checkbox | ❌ No | false | Gold badge |
| Diamond Store | Checkbox | ❌ No | false | Diamond badge |
| Free Shipping | Checkbox | ❌ No | false | Adds tag + 2HD badge |
| Free Gift | Checkbox | ❌ No | false | Adds tag |
| Flash Sale | Checkbox | ❌ No | false | Adds tag + icon |

---

## 🚀 Testing the Feature

### Test Cases:

**1. Image Upload:**
```
✅ Click camera FAB
✅ Select image from gallery
✅ Image displays in preview
✅ Image URI saved
```

**2. Official Store:**
```
✅ Check official store
✅ Brand auto-fills if empty
✅ Gold badge appears
✅ Official icon shows in card
```

**3. Diamond Store:**
```
✅ Check diamond store
✅ Diamond badge appears
✅ Overrides official store
✅ Diamond icon shows in card
```

**4. Flash Sale:**
```
✅ Check flash sale
✅ Tag added to product
✅ Flash sale icon shows
```

**5. Combined:**
```
✅ All checkboxes work together
✅ Multiple tags added
✅ Badges display correctly
✅ Product saves successfully
```

---

## 💡 Smart Features

### 1. **Auto Brand Detection**
```kotlin
// If official store checked but no brand entered
// Use first word of product name
"Samsung Galaxy S24" → Brand: "Samsung"
"Nike Air Max" → Brand: "Nike"
"iPhone 15 Pro" → Brand: "iPhone"
```

### 2. **Badge Priority**
```kotlin
// Diamond overrides Official
Diamond Store ✅ + Official Store ✅ = Diamond Badge
Diamond Store ❌ + Official Store ✅ = Gold Badge
Diamond Store ❌ + Official Store ❌ = No Badge
```

### 3. **Logistic Badge**
```kotlin
// Free shipping adds 2HD badge
Free Shipping ✅ = logisticBadge: "2HD"
Free Shipping ❌ = logisticBadge: ""
```

---

## 🎯 Summary

### What Was Added:

✅ **Real image upload** from device gallery  
✅ **Image preview** with Glide  
✅ **Official Store** checkbox (Gold badge)  
✅ **Diamond Store** checkbox (Diamond badge)  
✅ **Flash Sale** checkbox (adds tag)  
✅ **Auto brand detection** from product name  
✅ **Badge priority system** (Diamond > Gold > None)  
✅ **Logistic badge** for free shipping  
✅ **Proper permissions** for Android 13+  

### User Benefits:

🎨 Upload real product images  
🏪 Mark as official/diamond store  
⚡ Add flash sale indicator  
🚚 Show shipping badges  
💎 Display store quality badges  
✨ Professional product listings  

The Add Product feature is now complete with all the badges and tags that match your existing product display system! 🎉
