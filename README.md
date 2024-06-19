Test application created in Android Studio Koala 2024.1.1.

Goal of application is to showcase a failure to continue pagination if a Lazy list leaves the UI and returns. While this affects navigation, this demonstration uses a HorizontalPager to achieve the same results. 

To reproduce the issue:
1. Build and launch app.
2. You should initially see a LazyRow of data items, each displaying "Hello Title: #!". Each page is 40 items, and you should be able to continously load more pages by scrolling the LazyRow.
3. This is contained in a HorizontalPager that will switch to the 2nd page either when swiped just below the LazyRow, or when clicking an item in the list. Doing so will remove the list from the visible UI.
4. Swipe back to the first page to observe the issue: try to continue scrolling the LazyRow to load more pages. You should hit an end to the LazyRow, no longer able to load more pages.
5. A pull-to-refresh behavior is implemented as well, and you can swipe down to reset the list back to it's initial state. You should be able to repeat these steps again to observe it again.


Known Issue(s):
1. If you swipe Pager and swipe back, the Lazy list will appear to "jump" count, but really starting at 40 instead of 0 (a load is observed when swiping). This only happens once in my experience. Pulling to refresh and trying again should then preserve actual position in list. Either way, the failure to paginate more data is observable in both cases.
