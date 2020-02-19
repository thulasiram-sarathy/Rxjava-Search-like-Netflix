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

</br></br>

### RxJava 
For the basic implementation of a search view in RxJava I have used four operators namely debounce, filter, distinctUntilChanged and switchMap.</br></br>