# Edit and Delete Product Feature

## Overview
Added edit and delete functionality to product cards in the search page using a three-dot overflow menu, allowing users to manage products directly from the product list.

## Changes Made

### 1. Layout Updates
- **card_product_grid.xml**: Replaced single "Lihat" button with three-dot menu icon (ImageView)
- **card_product.xml**: Replaced single "Lihat Produk" button with three-dot menu icon (ImageView)
- **product_menu.xml**: Created new menu resource with Edit and Delete options

### 2. Product Model Updates
- Added computed `id` property that generates unique ID based on product name
- ID is created by converting name to lowercase, replacing special characters with hyphens
- Example: "Samsung Galaxy S21" → "samsung-galaxy-s21"

### 3. ProductAdapter Updates
- Added `onEditClick` and `onDeleteClick` callback parameters to the adapter constructor
- Updated `productData()` and `productDataGrid()` extension functions to accept edit/delete callbacks
- Implemented PopupMenu on three-dot icon click to show Edit/Delete options
- Menu items trigger the appropriate callbacks with product and position

### 3. ProductFragment Updates
- Added `handleEditProduct()` method to navigate to AddProductActivity in edit mode
- Added `handleDeleteProduct()` method with confirmation dialog
- Integrated edit/delete callbacks when creating the ProductAdapter
- Delete operation removes item from list and updates the UI

### 4. SearchViewModel Updates
- Added `deleteProduct()` method to remove products from both allProducts and currentSourceList
- Integrated with MainRepository to persist deletion

### 5. Menu Styling
- **Three-dot icon**: Uses Android's built-in `ic_menu_more` icon
- **Menu items**: Edit (with edit icon) and Delete (with delete icon)
- **Interaction**: Ripple effect on icon click, popup menu appears anchored to the icon

## User Flow

### Edit Product
1. User clicks three-dot menu icon on any product card
2. Popup menu appears with "Edit" and "Delete" options
3. User selects "Edit"
4. App navigates to AddProductActivity with product ID and edit mode flag
5. User can modify product details (implementation pending in AddProductActivity)

### Delete Product
1. User clicks three-dot menu icon on any product card
2. Popup menu appears with "Edit" and "Delete" options
3. User selects "Delete"
4. Confirmation dialog appears asking "Are you sure you want to delete [Product Name]?"
5. If user confirms:
   - Product is removed from the displayed list
   - Product is deleted from persistent storage
   - Snackbar shows "Product deleted" confirmation
4. If user cancels, dialog closes with no changes

## Technical Details

### Menu Icon Layout (Grid View)
```xml
<ImageView id="iv_menu" - 24dp x 24dp, positioned at bottom-right />
```

### Menu Icon Layout (List View)
```xml
<ImageView id="iv_menu" - 32dp x 32dp, positioned at bottom-right />
```

### Product ID Generation
```kotlin
val id: String
    get() = name.lowercase()
        .replace(Regex("[^a-z0-9]"), "-")
        .replace(Regex("-+"), "-")
        .trim('-')
```

### Callback Signature
```kotlin
onEditClick: ((Product, Int) -> Unit)?
onDeleteClick: ((Product, Int) -> Unit)?
```

## Future Enhancements
- Implement actual edit functionality in AddProductActivity
- Add undo option for delete action
- Add batch delete functionality
- Add swipe-to-delete gesture
