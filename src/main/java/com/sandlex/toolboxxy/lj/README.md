### Check the year and month when your account was created
https://sandlex.livejournal.com/profile/

### Export all records month by month.
https://www.livejournal.com/export.bml

### Prepare content directory
Copy all files to a single directory: /Users/apeskov/Documents/blog/ljexport
Sort by size and delete empty files (wich have only header and no content).
Create a copy of directory: /Users/apeskov/Documents/blog/ljexport_copy
Create directory md inside /Users/apeskov/Documents/blog/ljexport
Run PostConverter.java with argument /Users/apeskov/Documents/blog/ljexport
Check the number of md files created in md directory. It should match the number of records in your journal.
Copy directory md to /Users/apeskov/Documents/blog, create a copy /Users/apeskov/Documents/blog/md_copy and rename md into posts.

### Extract tags and add them to the posts
Run TagsExtractor.java with argument /Users/apeskov/Documents/blog.

Example:

```
Processing post 541381...
adding tags -> "триатлон", "бег", "неделя", "планы", "тренировки"
done with the post 541381

Processing post 45909...
no tags... skipping
done with the post 45909
```

### Download images
Create directory images in /Users/apeskov/Documents/blog/.
Create a placeholder image: /Users/apeskov/Documents/blog/images/image-not-found.png
Run ImageDownloader.java with argument /Users/apeskov/Documents/blog
It will go through each post, finds all img tags, downloads the image and saves it into /images/<post_id> directory. Name of the image will be in the format <post_id>_<image index in the post>.<original ext>.

Example:

```
Processing post 226431...
found images: 4
downloading image 1/4
downloading image 2/4
downloading image 3/4
downloading image 4/4
all images downloaded
done with the post 226431

Processing post 139719...
found images: 0
no images
done with the post 139719
```

If the program fails check the post which was processed. Correct tags if needed. Then delete image directory for this post and restart program again. All posts for which image directory exist will be skipped.

### Replacing lj user tags with URL
Run UsersReplacer.java with argument /Users/apeskov/Documents/blog

Example:
```
Processing post 33372...
<lj user="_blader_"> -> [_blader_](https://_blader_.livejournal.com)
<lj user="_blader_"> -> [_blader_](https://_blader_.livejournal.com)
<lj user="shun_gtr"> -> [shun_gtr](https://shun_gtr.livejournal.com)
done with the post 33372

Processing post 429927...
no links
done with the post 429927
```

### Replacing links
Run LinksReplacer.java with argument /Users/apeskov/Documents/blog

Example:
```
Processing post 283536...
no links
done with the post 283536

Processing post 479617...
<a href="http://sandlex.livejournal.com/478963.html">предложенному расписанию</a> -> [предложенному расписанию](478963.md)
done with the post 479617

Processing post 514580...
<a href="https://www.strava.com/activities/401652995">Сбегал, посмотрел</a> -> [Сбегал, посмотрел](https://www.strava.com/activities/401652995)
done with the post 514580
```
