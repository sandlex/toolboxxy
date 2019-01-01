renames files in the directory into a format with modification date:

`for f in dir/* ; do mv -n "$f" "$(date -r "$f" +'%Y-%m-%d %H-%M-%S').md"; done`

replaces `<br>` with the new line character in all files containing `<br>`:
`grep -rli '<br>' ./filename.md | xargs -I@ sed -i '' $'s/<br>/\\\n/g' @`