# IconFinderApp
This application integrates the [IconFinder API](https://developer.iconfinder.com/reference/overview-1) following the [best practices](https://developer.android.com/jetpack/guide#common-principles) in Android. 

##### Check out this [documentation as a GitHub Pages here](https://kavinraju.github.io/IconFinderApp/).

#### Currently it has 5 screens
1. IconSet List
2. Icons List (searchable)
3. IconSet Details along with the list of Icons under the IconSet
4. Icon Details with an option to download the icon
5. Author details along with the list of IconSet associated with the author

### Architecture used
<a href="https://developer.android.com/topic/libraries/architecture/paging#support-different-data-arch"><img src="https://developer.android.com/topic/libraries/architecture/images/paging-library-data-flow.png" width="350px"/>Source</a>

- Uses the network only data architecture as mentioned in [here](https://developer.android.com/topic/libraries/architecture/paging#support-different-data-arch). 
- Network + Local Database implementation was started in [paging-3-with-pending-implementation-item-keyed-data-source](https://github.com/kavinraju/IconFinderApp/tree/paging-3-with-pending-implementation-item-keyed-data-source) branch but since I couldn't find the right implementation for this [IconSet IconFinder API](https://developer.iconfinder.com/reference/overview-1) API as it is an `ItemKeyedDataSource`. This query has been raised in [stackoverflow](https://stackoverflow.com/questions/67951960/paging-3-library-calls-the-load-method-recursively-with-loadtype-append?noredirect=1#comment120127150_67951960).
- App's Navigation
<img width="917" alt="Screenshot 2021-06-18 at 8 02 42 PM" src="https://user-images.githubusercontent.com/24537737/122577355-51124b00-d070-11eb-9ad5-27ceb0945a4f.png">
- Shared Preferences for storing the user's filter option preference
- Local ROOM Db to store the `offset` values while making the [search icons API call](https://developer.iconfinder.com/reference/search), so that by increasing the value fro 0 to 100 we can get different sets of data. For reference see the [API docs](https://developer.iconfinder.com/reference/search), [implementation](https://github.com/kavinraju/IconFinderApp/blob/main/app/src/main/java/com/srilasaka/iconfinderapp/data/SearchIconsPagingSource.kt). But further this project could be extended to load data from local DB using Network + Local Database architecture.
 
### About branches
- `main` has all the working code
- `development` consists of code pushed during the development phase
- `paging-3-with-pending-implementation-item-keyed-data-source` contains the Network + Local Database implementation of the Paging 3 library.
- `test` this branch (not yet created, but will be created soon) could be used for implementing the tests, before the code is being merged to the `main` branch

### Screenshots
##### Light Mode
<img src="https://user-images.githubusercontent.com/24537737/122581296-87ea6000-d074-11eb-9351-4ce43ead61cc.png" width="300px"/> <img src="https://user-images.githubusercontent.com/24537737/122581319-8caf1400-d074-11eb-948f-721fa3f99a46.png" width="300px"/> <img src="https://user-images.githubusercontent.com/24537737/122581324-8de04100-d074-11eb-8a80-4347d1568286.png" width="300px"/> <img src="https://user-images.githubusercontent.com/24537737/122581810-10690080-d075-11eb-9ec7-69dc78b69d18.png" width="300px"/> <img src="https://user-images.githubusercontent.com/24537737/122581824-1363f100-d075-11eb-88d7-d44d1be1dcc7.png" width="300px"/> <img src="https://user-images.githubusercontent.com/24537737/122581839-1828a500-d075-11eb-9294-83f057d86544.png" width="300px"/>
<img src="https://user-images.githubusercontent.com/24537737/122582132-69d12f80-d075-11eb-83eb-7e2165f16802.png" width="300px"/>
<img src="https://user-images.githubusercontent.com/24537737/122582131-69389900-d075-11eb-9752-1ddd8ebf9900.png" width="300px"/>
<img src="https://user-images.githubusercontent.com/24537737/122582135-6a69c600-d075-11eb-9a1d-0322bc7f9387.png" width="300px"/>
<img src="https://user-images.githubusercontent.com/24537737/122582128-68a00280-d075-11eb-9b19-1224389f0d23.png" width="300px"/>
<img src="https://user-images.githubusercontent.com/24537737/122582109-6473e500-d075-11eb-9885-ea5be95ddd1f.png" width="300px"/>

##### Dark Mode
<img src="https://user-images.githubusercontent.com/24537737/122582636-fed42880-d075-11eb-8ab4-c754664ad45c.png" width="300px"/> <img src="https://user-images.githubusercontent.com/24537737/122582652-0267af80-d076-11eb-9cbd-ad07ff2fc387.png" width="300px"/> <img src="https://user-images.githubusercontent.com/24537737/122582656-03004600-d076-11eb-919a-b4f5d29a6b69.png" width="300px"/> <img src="https://user-images.githubusercontent.com/24537737/122582659-0398dc80-d076-11eb-8593-1b6ef29cab8a.png" width="300px"/> <img src="https://user-images.githubusercontent.com/24537737/122582663-04317300-d076-11eb-9730-57573f0f935a.png" width="300px"/>

<img src="https://user-images.githubusercontent.com/24537737/122582798-304cf400-d076-11eb-829b-70f697b0e0c9.png" width="300px"/><img src="https://user-images.githubusercontent.com/24537737/122582810-33e07b00-d076-11eb-9f35-97f5099ff923.png" width="300px"/> <img src="https://user-images.githubusercontent.com/24537737/122582811-34791180-d076-11eb-853d-2f8b772f9214.png" width="300px"/> <img src="https://user-images.githubusercontent.com/24537737/122582814-35aa3e80-d076-11eb-8139-e28672c3198b.png" width="300px"/> <img src="https://user-images.githubusercontent.com/24537737/122582818-3642d500-d076-11eb-879b-857bcc1541f2.png" width="300px"/>

### Screen Recordings

##### Light Mode
<img src="https://user-images.githubusercontent.com/24537737/122578080-270d5880-d071-11eb-9c1e-4652fb5be372.gif" width="350px"/>

##### Dark Mode
<img src="https://user-images.githubusercontent.com/24537737/122578164-3e4c4600-d071-11eb-8e14-6237f10a9d19.gif" width="350px"/>
    
##### Author details screen
<img src="https://user-images.githubusercontent.com/24537737/122578359-6d62b780-d071-11eb-9399-85bb62473a21.gif" width="350px"/>
 
### Libraries used
- [Android Architecture Components](https://developer.android.com/topic/libraries/architecture)
  - Coroutines
  - DataBinding
  - Navigation
  - ViewModel
  - LiveData
  - Room
  - Paging 3
- [Glide](https://bumptech.github.io/glide/) for loading image
- [Kotlin Flow](https://developer.android.com/kotlin/flow) used to emit values from `suspend` functions which are being used in Repository to fetch data from network or database layer.
- [Material Design](https://material.io/components?platform=android)
- [Retrofit](https://square.github.io/retrofit/) for making API calls
- [OkHttp interceptor](https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor) for logging the API responses
