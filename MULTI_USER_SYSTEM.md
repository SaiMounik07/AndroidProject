# Multi-User System Implementation

## Overview
Implemented a complete multi-user system that stores user credentials and maintains separate product lists for each user.

## Architecture

### 1. User Storage
Users are stored in SharedPreferences as a JSON array:
```json
{
  "USERS": [
    {"username": "sai", "password": "sai"},
    {"username": "user", "password": "user"},
    {"username": "john", "password": "pass123"}
  ]
}
```

### 2. Product Storage
Products are stored per user using username as the key:
```
SharedPreferences:
- "sai" → [Product1, Product2, Product3]
- "user" → [Product4, Product5]
- "john" → [Product6]
```

## Implementation Details

### User Registration (addUser)
```kotlin
fun addUser(username: String, password: String) {
    // Get existing users
    val existing = mainRepository.getValueByKey("USERS", "[]")
    val arr = JSONArray(existing)
    
    // Create new user object
    val user = JSONObject().apply {
        put("username", username)
        put("password", password)
    }
    
    // Add to array and save
    arr.put(user)
    mainRepository.saveValueByKey("USERS", arr.toString())
}
```

**Features:**
- Stores multiple users in a single JSON array
- Preserves existing users when adding new ones
- Simple username/password structure

### Login Validation (validateLogin)
```kotlin
fun validateLogin(
    name: String,
    password: String,
    onSuccess: () -> Unit,
    onFailure: () -> Unit
) {
    // Validate input
    if (name.isEmpty() || password.isEmpty()) {
        onFailure.invoke()
        return
    }
    
    // Get all users
    val usersJson = mainRepository.getValueByKey("USERS", "[]")
    val usersArray = JSONArray(usersJson)
    
    // Check credentials
    var isValid = false
    for (i in 0 until usersArray.length()) {
        val user = usersArray.getJSONObject(i)
        val storedUsername = user.getString("username")
        val storedPassword = user.getString("password")
        
        if (name == storedUsername && password == storedPassword) {
            isValid = true
            // Save current logged-in user
            mainRepository.saveValueByKey(USERNAME, name)
            break
        }
    }
    
    if (isValid) {
        onSuccess.invoke()
    } else {
        onFailure.invoke()
    }
}
```

**Features:**
- Validates against all stored users
- Saves current username to SharedPreferences
- Callback-based success/failure handling
- Early return for empty inputs

### Product Management Per User

#### Save Product
```kotlin
fun saveProduct(product: Product) {
    val username = mainRepository.getValueByKey(USERNAME, GUEST)
    mainRepository.saveProduct(username.toString(), product)
}
```

#### Get Products
```kotlin
fun getProducts(): List<Product> {
    val username = mainRepository.getValueByKey(USERNAME, GUEST)
    return mainRepository.getProducts(username.toString())
}
```

#### Delete Product
```kotlin
fun deleteProduct(product: Product) {
    val username = mainRepository.getValueByKey(USERNAME, GUEST)
    mainRepository.deleteProduct(username.toString(), product)
}
```

**Key Points:**
- All operations use current logged-in username
- Falls back to "GUEST" if no user logged in
- Products are completely isolated per user

## Data Flow

### Login Flow
```
1. User enters credentials
   ↓
2. ConstraintLoginActivity calls validateLogin()
   ↓
3. ConstraintViewModel checks USERS array
   ↓
4. If valid: Save USERNAME to SharedPreferences
   ↓
5. Navigate to HomeActivity
   ↓
6. Load products for logged-in user
```

### Product Operations Flow
```
1. User adds/edits/deletes product
   ↓
2. ViewModel gets current USERNAME
   ↓
3. Operation performed on user-specific key
   ↓
4. Data saved: SharedPreferences[username] = products
```

### Logout Flow
```
1. User clicks logout
   ↓
2. Clear USERNAME from SharedPreferences
   ↓
3. Navigate to LoginActivity
   ↓
4. Next login will load different user's products
```

## Usage Examples

### Example 1: Register Users
```kotlin
// In ConstraintLoginActivity onCreate()
constraintViewModel.addUser("sai", "sai")
constraintViewModel.addUser("user", "user")
constraintViewModel.addUser("admin", "admin123")
```

### Example 2: Login
```kotlin
// User enters: username="sai", password="sai"
constraintViewModel.validateLogin(
    name = "sai",
    password = "sai",
    onSuccess = {
        // USERNAME saved as "sai"
        // Navigate to HomeActivity
        startActivity(Intent(this, HomeActivity::class.java))
    },
    onFailure = {
        Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
    }
)
```

### Example 3: Add Product (User: sai)
```kotlin
// User "sai" adds a product
val product = Product(name = "Laptop", ...)
viewModel.saveProduct(product)

// Stored in: SharedPreferences["sai"] = [Laptop]
```

### Example 4: Switch Users
```kotlin
// User "sai" logs out
// User "user" logs in

// User "user" sees only their products
val products = viewModel.getProducts()
// Returns: SharedPreferences["user"] products only
// Does NOT see "sai"'s products
```

## Security Considerations

### Current Implementation
⚠️ **Note**: This is a basic implementation for learning purposes

**Limitations:**
1. Passwords stored in plain text
2. No encryption
3. SharedPreferences is not secure storage
4. No password hashing
5. No session management

### Production Recommendations
For a production app, implement:

1. **Password Hashing**
```kotlin
// Use BCrypt or similar
val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
```

2. **Encrypted SharedPreferences**
```kotlin
val masterKey = MasterKey.Builder(context)
    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
    .build()

val encryptedPrefs = EncryptedSharedPreferences.create(
    context,
    "secure_prefs",
    masterKey,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
)
```

3. **Backend Authentication**
- Use JWT tokens
- OAuth 2.0
- Firebase Authentication
- Server-side validation

4. **Secure Storage**
- Android Keystore
- Room Database with encryption
- Backend database

## Testing Scenarios

### Test 1: Multiple Users
```
1. Register users: "alice", "bob", "charlie"
2. Login as "alice"
3. Add products: Product A, Product B
4. Logout
5. Login as "bob"
6. Verify: No products visible
7. Add products: Product C
8. Logout
9. Login as "alice"
10. Verify: Product A, Product B visible (not Product C)
```

### Test 2: Invalid Login
```
1. Enter username: "test"
2. Enter password: "wrong"
3. Click login
4. Expected: Error message, stay on login screen
```

### Test 3: Empty Credentials
```
1. Leave username empty
2. Enter password: "test"
3. Click login
4. Expected: Validation error
```

### Test 4: Product Isolation
```
1. Login as "user1"
2. Add 5 products
3. Logout
4. Login as "user2"
5. Add 3 products
6. Logout
7. Login as "user1"
8. Expected: See 5 products (not 8)
```

## Current Users

The app comes pre-configured with these test users:
```kotlin
Username: "sai"     Password: "sai"
Username: "user"    Password: "user"
```

You can add more users by calling:
```kotlin
constraintViewModel.addUser("newuser", "password123")
```

## Data Persistence

### SharedPreferences Structure
```
File: PREFS_FILE_LOGIN
├── USERS: "[{username, password}, ...]"
├── USERNAME: "current_logged_in_user"
├── sai: "[Product1, Product2, ...]"
├── user: "[Product3, Product4, ...]"
└── admin: "[Product5, ...]"
```

### Data Lifecycle
- **App Install**: Empty SharedPreferences
- **First Launch**: Users registered in onCreate()
- **Login**: USERNAME saved
- **Add Product**: Saved under USERNAME key
- **Logout**: USERNAME cleared
- **App Uninstall**: All data deleted

## Advantages

1. **User Isolation**: Each user has separate product list
2. **Persistent Storage**: Data survives app restarts
3. **Simple Implementation**: Easy to understand and maintain
4. **No Backend Required**: Works offline
5. **Fast Access**: Local storage, no network calls

## Limitations

1. **No Cloud Sync**: Data only on device
2. **No User Management**: Can't reset password, etc.
3. **Basic Security**: Not suitable for sensitive data
4. **No Validation**: No email format, password strength checks
5. **Single Device**: Can't access data from other devices

## Future Enhancements

1. **User Registration Screen**: Allow users to create accounts
2. **Password Reset**: Forgot password functionality
3. **Profile Management**: Edit user details
4. **Cloud Sync**: Firebase/Backend integration
5. **Biometric Auth**: Fingerprint/Face unlock
6. **Session Timeout**: Auto-logout after inactivity
7. **Remember Me**: Stay logged in option
8. **User Roles**: Admin, User, Guest permissions
