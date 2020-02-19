# Rxjava-Search-like-Netflix
## Android Search using Rxjava (Epoxy Recyclerview, Retrofit)

In this article I have implemented Android search using RxJava for instant search results. Various libraries such as Airbnb’s Epoxy recyclerview, Retrofit for network call is implemented for fetching results from the TMDB api.</br></br>

<img src="https://github.com/thulasiram-sarathy/Rxjava-Search-like-Netflix/blob/master/media/searchview.gif" style="max-width:100%;">
</br></br>

## Getting started

### Usages:
1. RxJava
2. Epoxy library
3. Retrofit
4. TMDB API


### RxJava 
For the basic implementation of a search view in RxJava I have used four operators namely debounce, filter, distinctUntilChanged and switchMap.</br></br>
<b>Debounce <b>: Returns an Observable that mirrors the source ObservableSource, except that it drops items emitted by the source ObservableSource that are followed by newer items before a timeout value expires. The timer resets on each emission.</br>
In Other words using the debounce will prevent the search request or letters being sent to the server immediately, meaning debounce(300,TimeUnit.MILLISECONDS) will wait for 300 milliseconds in sending every request to the server. If your search query is “armageddon” and for each the my character tying speed is 100 milliseconds per letter then the request query sent to the server would be arm, armage, armageddo since it debounce wait for 300 ms of every request.

If no debounce is used the request to the server be like a, ar, arm, arma…. 4 network call is being made for 4 characters typed which is a costly one. </br>

<b>Filter <b>: Filters items emitted by an ObservableSource by only emitting those that satisfy a specified predicate.</br>
Filter operator are used in the search feature to filter out needless queries such as empty requests or if I need to send network request for queries more than 2 characters filter operator can be used. Suppose if, filter{it >2} is used and typing a search query the network request is sent only after tying arm because it allows network calls to be made only the queries greater than 2 characters.</br>


<b>DistinctUntilChanged<b>: Returns an Observable that emits all items emitted by the source ObservableSource that are distinct from their
immediate predecessors.</br>
distinctUntilChanged operator is used to avoid unnecessary or identical network calls. This operator stops request to sent to the server if has already fetched, eg: if the my current query is “Dunkr” and I cancelling out the typed query by a character say “Dunk” since a network request has already been sent for “Dunk” the distinctUntilChanged operator wont allow a new network call instead it will retrieve from the previous result.</br>

<b>SwitchMap<b>: Returns a new ObservableSource by applying a function that you supply to each item emitted by the source ObservableSource that returns an ObservableSource, and then emitting the items emitted by the most recently emitted of these ObservableSources.</br>
switchMap is as powerful operator very much like other operators. Suppose if the search query is “Dunk “ and the network call is made and the observer is subscribed for this result and in the meantime another query is made which is for “Dunkr” and a network call is again made, what the switchMap operator does is that it helps the observer to be subscribed to the latest query which is “Dunkr” since the network response for the query “Dunk” is no longer required.</br></br>

###Epoxy Implementation:
Epoxy is an Android library for building complex screens in a RecyclerView.</br>
<b>build.gradle<b>
```
    dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation"org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"
    implementation 'androidx.appcompat:appcompat:1.1.0'
    implementation 'androidx.core:core-ktx:1.2.0'
    implementation 'androidx.constraintlayout:constraintlayout:1.1.3'

    //Airbnb Epoxy
    implementation "com.airbnb.android:epoxy:$epoxyVersion"
    kapt "com.airbnb.android:epoxy-processor:$epoxyVersion"
    implementation "com.airbnb.android:epoxy-paging:$epoxyVersion"

    implementation 'android.arch.lifecycle:extensions:1.1.1'

    implementation 'androidx.cardview:cardview:1.0.0'

    /* Retrofit - Networking library  */
    implementation 'com.squareup.retrofit2:retrofit:2.4.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.4.0'
    implementation 'com.squareup.retrofit2:adapter-rxjava2:2.4.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:3.9.1'

    /* Picasso - Image Loading library  */
    implementation 'com.squareup.picasso:picasso:2.71828'

    /*RXJava*/
    implementation 'io.reactivex.rxjava2:rxjava:2.1.9'
    implementation 'io.reactivex.rxjava2:rxandroid:2.0.2'

    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'androidx.test.ext:junit:1.1.1'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.2.0'
    androidTestImplementation 'androidx.test:rules:1.3.0-alpha02'
    }
```
</br>
<b>SearchController.kt</b>
```
class SearchController(private val listener:SearchClickListener) :TypedEpoxyController<List<SearchResponse.MovieApiResponse>>(){

    override fun buildModels(data: List<SearchResponse.MovieApiResponse>) {
        data.forEach {
            HomeViewModel_()
            .id(it.id)
            .image(it.poster_path)
            .moviename(it.title)
            .movieRating(it.voteAverage.div(2).toFloat())
            .movieOnClickListener { _, _, _, _ ->
                listener.onSearchMovieItemClicked(it)
            }
                .addTo(this)
        }

    }

}
```</br></br>
<b>EpoxyView.kt</b>
```
@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT,fullSpan = false)
class HomeView @JvmOverloads constructor(context: Context,
                                          attributes: AttributeSet?=null,
                                          defStyle:Int = 0)
    :CardView(context,attributes,defStyle)
{

    private val posterImage: ImageView
    private val movieName: TextView
    private val movieRating: RatingBar
    private val movielayouyView: CardView

    init {
        View.inflate(context, R.layout.movie_item,this)

        posterImage = findViewById(R.id.posterImageView)
        movieName = findViewById(R.id.movieName)
        movieRating = findViewById(R.id.movieRating)
        movielayouyView = findViewById(R.id.movieCardLayout)
    }

    @ModelProp
    fun setImage(url:String?)
    {
        posterImage.loadMovieImage(url)

    }

    @ModelProp
    fun setMoviename(movie:String?)
    {
        movieName.text = movie

    }
    @ModelProp
    fun setMovieRating(rating:Float)
    {
        movieRating.rating = rating

    }

    @CallbackProp
    fun setMovieOnClickListener(listener: OnClickListener?) {
        movielayouyView.setOnClickListener(listener)
    }


}
```</br></br>
<b>SearchActivty.kt</b>
```
       val searchListController = SearchController(object : SearchClickListener {
            override fun onSearchMovieItemClicked(movie: SearchResponse.MovieApiResponse) {

            }
        })

        searchRecyclerview.adapter =searchListController.adapter
        searchRecyclerview.setItemSpacingDp(4)

        searchViewModel.apiMovieData().observe(this , Observer {
            searchListController.setData(it)
        })

        searchRecyclerview.setupGridManager(searchListController)
```</br></br>
<b>SearchViewModel.kt</b>
```
    fun fetchMoviesList(query: String){
            val observable = restApi.getMoviesList(query,TMDB_KEY)
            observable?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.compose(RxSingleSchedulers.DEFAULT.applySchedulers<SearchResponse>())
                ?.subscribe({ loginResponse ->
                    apiData.postValue(loginResponse)

                }, { error ->
                   // handle loading during error
                }
                )

    }

   
    fun fetchMovie(){
        val observable = Observable.just(apiData.value?.results)
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { loginResponse ->
                getMovieLiveData.postValue(loginResponse)
            }
    }

```</br></br>
<b>RestApi.kt</b>
```
interface RestApi {

    @GET("search/movie")
    fun getMoviesList(@Query("query") query: String?,@Query("api_key") api_key:String
    ): Single<SearchResponse>

    companion object {

        fun create(httpUrl: HttpUrl): RestApi {
            val okHttpClient = OkHttpClient.Builder()
                .build()
            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
                .create(RestApi::class.java)
        }


        fun create(): RestApi = create(HttpUrl.parse(BASE_URL)!!)
    }

}
```</br></br>

Thanks for going through my blog post and send me feedback so that I could update myself for future posts.</br>
!!!! Cheers !!!!
