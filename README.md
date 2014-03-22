MuSync
======

MuSync is an Android music player app. I had an idea for a social twist where you could see what your friends were listening to on their devices in real time, as well see what songs/artists were trending in your area or others. I've since scrapped the social aspect since [Soundwave](https://play.google.com/store/apps/details?id=me.soundwave.soundwave) launched a couple weeks after I started this project. Soundwave is an awesome app, and the team is working really hard on making it even better. Check it out if you get a chance.

I still think MuSync has potential to be a solid music player, which is why I've open sourced it so other devs can contribute/modify it as they see fit. 


As of this writing, MuSync uses these open source libraries.

#* [ActionBarSherlock](https://github.com/JakeWharton/ActionBarSherlock)
#* [ViewPagerIndicator](https://github.com/JakeWharton/Android-ViewPagerIndicator)
#* [Android Visualizer](https://github.com/felixpalmer/android-visualizer)
#* [Umano's Sliding Up Panel](https://github.com/umano/AndroidSlidingUpPanel) 


Screenshots

<img src=http://i.imgur.com/hhrjADA.png height="600" width="350">
<img src=http://i.imgur.com/p8PKAt5.png height="600" width="350">
<img src=http://i.imgur.com/tq6auEf.png height="600" width="350">
<img src=http://i.imgur.com/i0jRl4W.png height="600" width="350">



Feel free to clone it and contribute, thanks for checking it out!

#Mistakes I made/Observations

1) **Use a Bounded Service instead for music playback**
At the time, I created a background service to handle the playback and made it a singleton, and whenever I needed to get a hold of it I'd just call Service.getInstance() from my Activity/Fragment or whatever to control the playback. You don't want to do this. Instead, use a [Bounded Service](http://developer.android.com/guide/components/bound-services.html), and register your Activity or Fragment as clients using a binder and service connection. The documentation explains this idea in detail and I came across it a couple months after I had started my horrible implementation. It's all a learning process though.

2) There's currently no mechanism to manage the lifecycle of the AsyncTasks, or to check if they are finished doing their jobs. This can cause nasty memory leaks as you're navigating between fragments and firing off new tasks before a previous one is finished.


3) I was using [LRU caches](http://developer.android.com/reference/android/util/LruCache.html) to cache the images(album art, genre art, etc). At that point I was allocating 1/8 of the apps heap memory for the cache, [as recommended](http://developer.android.com/training/displaying-bitmaps/cache-bitmap.html). You might want to use another image caching library if you think it would be more optimal.


#License

Copyright 2013 Jade Byfield

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
