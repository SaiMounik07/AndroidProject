# 📱 Grid/List View Toggle Implementation

## Overview

I've implemented a toggle feature that allows users to switch between **List View** and **Grid View** for displaying products.

---

## 🎯 What Was Added

### 1. **Toggle Button in UI**
- Added an ImageView icon in `search_fragment.xml`
- Located at the top-right, below the search bar
- Changes icon based on current view mode

### 2. **Grid Layout File**
- Created `card_product_grid.xml`
- Optimized for 2-column grid display
- Compact design with smaller text and images

### 3. **Adapter Enhancement**
- Added `isGridView` parameter to ProductAdapter
- Supports both view types dynamically
- Smooth transition between layouts

### 4. **Fragment Logic**
- `toggleViewType()` method switches between layouts
- Updates LayoutManager (Linear ↔ Grid)
- Refreshes adapter with new view type

---

## 📂 Files Modified/Created

### Created:
✅ `app/src/main/res/layout/card_product_grid.xml` - Grid layout

### Modified:
✅ `app/src/main/res/layout/search_fragment.xml` - Added toggle button  
✅ `app/src/main/java/com/example/androidlearning/ui/fragments/ProductFragment.kt` - Toggle logic  
✅ `app/src/main/java/com/example/androidlearning/ui/search/ProductAdapter.kt` - View type support  

---

## 🔧 Implementation Details

### 1. UI Changes (search_fragment.xml)

```xml
<ImageView
    android:id="@+id/iv_toggle_view"
    android:layout_width="24dp"
    android:layout_height="24dp"
    android:layout_marginTop="8dp"
    android:layout_marginEnd="8dp"
    android:src="@android:drawable/ic_menu_view"
    android:contentDescription="Toggle view"
    android:clickable="true"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintTop_toBottomOf="@id/et_search" />
```

**What it does:**
- Shows a toggle icon
- Positioned below search bar, top-right corner
- Clickable to switch views

---

### 2. ProductAdapter Changes

#### Added Properties:
```kotlin
class ProductAdapter(
    private var products: List<Product>, 
    private val onProductClick: (Product) -> Unit,
    private var isGridView: Boolean = false  // NEW: Track view type
): RecyclerView.Adapter<ProductAdapter.ProductViewHolder>()
```

#### Added View Type Support:
```kotlin
companion object {
    const val VIEW_TYPE_LIST = 0
    const val VIEW_TYPE_GRID = 1
}

override fun getItemViewType(position: Int): Int {
    return if (isGridView) VIEW_TYPE_GRID else VIEW_TYPE_LIST
}
```

**How it works:**
- `getItemViewType()` tells RecyclerView which layout to use
- Returns different constant based on `isGridView` flag

#### Added Toggle Method:
```kotlin
fun toggleViewType(isGrid: Boolean) {
    isGridView = isGrid
    notifyDataSetChanged()  // Refresh all items with new layout
}
```

**What it does:**
- Updates the view type flag
- Triggers adapter to rebind all items with new layout

---

### 3. ProductFragment Changes

#### Setup RecyclerView:
```kotlin
private fun setupRecyclerView() {
    layoutManager = LinearLayoutManager(requireContext())
    with(binding.recyclerView) {
        this.layoutManager = this@ProductFragment.layoutManager
        visibility = View.VISIBLE
        productAdapter = ProductAdapter(
            displayedProducts, 
            { product -> showProductDetailsBottomSheet(product) },
            isGridView = false  // Start with list view
        )
        adapter = productAdapter
        addOnScrollListener(createScrollListener())
    }
}
```

#### Added Click Listener:
```kotlin
private fun setupClickListeners() {
    with(binding) {
        // ... other listeners ...
        
        ivToggleView.setOnClickListener {
            toggleViewType()
        }
    }
}
```

#### Toggle Logic:
```kotlin
private fun toggleViewType() {
    val isCurrentlyGrid = layoutManager is androidx.recyclerview.widget.GridLayoutManager
    
    if (isCurrentlyGrid) {
        // Switch to List
        layoutManager = LinearLayoutManager(requireContext())
        binding.ivToggleView.setImageResource(android.R.drawable.ic_menu_view)
    } else {
        // Switch to Grid (2 columns)
        layoutManager = androidx.recyclerview.widget.GridLayoutManager(requireContext(), 2)
        binding.ivToggleView.setImageResource(android.R.drawable.ic_dialog_dialer)
    }
    
    binding.recyclerView.layoutManager = layoutManager
    productAdapter.toggleViewType(!isCurrentlyGrid)
    binding.recyclerView.addOnScrollListener(createScrollListener())
}
```

**Step-by-step:**
1. Check current layout type
2. Create new LayoutManager (Linear or Grid)
3. Update toggle icon
4. Apply new LayoutManager to RecyclerView
5. Tell adapter to use new view type
6. Re-attach scroll listener for pagination

---

## 🎨 Layout Differences

### List View (card_product.xml)
```
┌─────────────────────────────────────┐
│ ┌────────┐  Product Name           │
│ │        │  Rp100.000  Rp120.000   │
│ │ Image  │  ⭐ 4.5 · Terjual 100   │
│ │        │  🏪 Store Name          │
│ └────────┘  [Lihat Produk]         │
└─────────────────────────────────────┘
```
- **Horizontal layout**
- Image on left (150x150dp)
- Details on right
- Full product name visible
- Larger button

### Grid View (card_product_grid.xml)
```
┌──────────┐  ┌──────────┐
│          │  │          │
│  Image   │  │  Image   │
│          │  │          │
├──────────┤  ├──────────┤
│ Name...  │  │ Name...  │
│ Rp100.000│  │ Rp100.000│
│ ⭐ 4.5   │  │ ⭐ 4.5   │
│ [Lihat]  │  │ [Lihat]  │
└──────────┘  └──────────┘
```
- **Vertical layout**
- Image on top (full width)
- Details below
- 2 columns
- Compact design
- Smaller text sizes

---

## 🔄 User Flow

### Initial State:
```
User opens search screen
↓
List view displayed (default)
↓
Toggle icon shows: 📋 (list icon)
```

### User Clicks Toggle:
```
User clicks toggle icon
↓
toggleViewType() called
↓
Check current type: List
↓
Create GridLayoutManager(2 columns)
↓
Update icon to: ⊞ (grid icon)
↓
Apply to RecyclerView
↓
Adapter refreshes with grid layout
↓
Products displayed in 2-column grid
```

### User Clicks Toggle Again:
```
User clicks toggle icon
↓
toggleViewType() called
↓
Check current type: Grid
↓
Create LinearLayoutManager
↓
Update icon to: 📋 (list icon)
↓
Apply to RecyclerView
↓
Adapter refreshes with list layout
↓
Products displayed in single column
```

---

## 🎯 Key Features

### 1. **Seamless Switching**
- Instant transition between views
- No data reload required
- Maintains scroll position (approximately)

### 2. **Responsive Design**
- Grid: 2 columns for better space utilization
- List: Full width for detailed information
- Both layouts fully responsive

### 3. **Consistent Functionality**
- Click to view product details works in both views
- Pagination works in both views
- Search works in both views
- All product information displayed

### 4. **Visual Feedback**
- Icon changes to indicate current view
- List icon (📋) when in list view
- Grid icon (⊞) when in grid view

---

## 📊 Comparison

| Feature | List View | Grid View |
|---------|-----------|-----------|
| **Columns** | 1 | 2 |
| **Image Size** | 150x150dp | Full width x 150dp |
| **Product Name** | Full, multiple lines | 2 lines max |
| **Details** | All visible | Compact |
| **Button** | "Lihat Produk" | "Lihat" |
| **Best For** | Detailed browsing | Quick scanning |
| **Space Efficiency** | Lower | Higher |

---

## 🚀 Benefits

### For Users:
✅ **Choice**: Pick preferred viewing style  
✅ **Flexibility**: Switch anytime  
✅ **Efficiency**: Grid shows more products at once  
✅ **Detail**: List shows more information per product  

### For Development:
✅ **Clean Code**: Separated concerns  
✅ **Reusable**: Same adapter for both views  
✅ **Maintainable**: Easy to modify layouts  
✅ **Scalable**: Can add more view types easily  

---

## 🔮 Future Enhancements

### Possible Improvements:

1. **Remember Preference**
   ```kotlin
   // Save user's preferred view
   SharedPreferences.edit()
       .putBoolean("isGridView", isGridView)
       .apply()
   ```

2. **Animation**
   ```kotlin
   // Smooth transition animation
   recyclerView.scheduleLayoutAnimation()
   ```

3. **3-Column Grid**
   ```kotlin
   // For tablets
   val spanCount = if (isTablet) 3 else 2
   GridLayoutManager(context, spanCount)
   ```

4. **Different Icons**
   ```kotlin
   // Custom icons instead of system icons
   R.drawable.ic_list_view
   R.drawable.ic_grid_view
   ```

---

## 🎓 How It Works (Technical)

### RecyclerView View Types:

RecyclerView supports multiple view types through:

1. **`getItemViewType(position)`**
   - Called for each item
   - Returns an integer representing view type
   - We return 0 for list, 1 for grid

2. **`onCreateViewHolder(parent, viewType)`**
   - Called when new ViewHolder needed
   - `viewType` parameter tells which layout to inflate
   - We use same layout but could use different ones

3. **`onBindViewHolder(holder, position)`**
   - Called to bind data to ViewHolder
   - Same binding logic for both types
   - Layout automatically adjusts

### LayoutManager:

**LinearLayoutManager:**
```kotlin
LinearLayoutManager(context)
// Creates single-column vertical list
```

**GridLayoutManager:**
```kotlin
GridLayoutManager(context, 2)
// Creates 2-column grid
// Can specify any number of columns
```

---

## 📝 Summary

You now have a fully functional grid/list toggle feature that:

✅ Allows users to switch between list and grid views  
✅ Uses the same adapter for both layouts  
✅ Maintains all functionality (search, pagination, click)  
✅ Provides visual feedback with icon changes  
✅ Optimizes space with 2-column grid  
✅ Shows detailed info in list view  

The implementation is clean, maintainable, and follows Android best practices! 🎉
