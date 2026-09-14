<h1>Product Catalog</h1>
<h3>Overview</h3>
<p>A native android product catalog application built in Android Studio with Java. In this application, it retrieves product data 
from  DUMMYJSON API and allows users to browse, search and view product details and reviews. </p>

<h3>Technologies Used</h3>
<ul>
  <li>Android Studio</li>
  <li>Java (Programming Language)</li>
  <li>Retrofit + Gson</li>
  <li>Picasso</li>
  <li>RecyclerView</li>
  <li>SwipeRefreshLayout</li>
  <li>DummyJSON API</li>
</ul>

<h3>Features</h3>
<ul>
  <li>Product listing with thumbnail, title, and price</li>
  <li>Pagination using skip and limit</li>
  <li>Tap feature for product details with rating, description, and reviews</li>
  <li>Product search with partial keywords</li>
  <li>Error state with Retry button</li>
  <li>Empty state for no search results</li>
  <li>Pull-to-refresh</li>
</ul>

<h3>Architecture</h3>
<p>The project use 3-layered layered structure to separate data-related responsibilities from the UI. </p>
<pre>
  **com.example.**
    |----data
    |    |------api
    |    |       |------ProductApi.java
    |    |
    |    |------model
    |    |       |------Product.java
    |    |       |------ProductResponse.java
    |    |       |------ProductReview.java
    |    |
    |    |------repository
    |    |       |------ProductRepository.java
    |
    |
    |----ui
         |------ProductActivity.java
         |------ProductAdapter.java
         |------ProductDetail.java
</pre>

<h3>Architecture Decision</h3>
<ul>
  <li>API layer handles Retrofit endpoint definitions and API communication</li>
  <li>Model layer represents the data returned by the API</li>
  <li>Repository layer provides a separation between the UI and API calls</li>
  <li>UI layer handles user interaction and displays the product data</li>
</ul>

<h3>API Flow</h3>
<pre>
  UI ---> Repository ---> API
</pre>
<p>For the product list and pagination, the application retrieves products using skip and limit.</p>
<p>For search, the application uses the DummyJSON search endpoint with a 500 ms debounce to avoid making a request for every character typed.</p>

<h3>How to Run</h3>
<p>##<b>Option 1:</b> Install the APK</p> 
<ol>
  <li>Open **apk file** and download the APK: [`apk/productCatalog.apk`](./apk/productCatalog.apk) in the git repo</li>
  <li>Transfer the APK to your Android device (via USB, Google Drive, email, etc.).</li>
  <li>On your device, open **Settings → Security → Install unknown apps** and allow installation for the app you're using to open the file.</li>
  <li>Tap the APK file → **Install** → **Open**.</li>
</ol>

<p>##<b>Option 2:</b> Run from Android Studio </p>
<ol>
  <li>Clone or download this repository</li>
  <li>Launch Android Studio, then file open the cloned or downloaded project folder in the Android Studio </li>
  <li>Sync the Gradle dependencies</li>
  <li>Run the application by connecting an Android device or start an Android emulator.</li>
  <li><b>***Make sure the device has an internet connection to access the DummyJSON API***</b></li>
</ol>

<h3>TODO / Future Improvement </h3>
<ul>
  <li>Add local caching to allow previously loaded products to be viewed offline.</li>
  <li>Display multiple product images as an image gallery on the product detail screen.</li>
</ul>
