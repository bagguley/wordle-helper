# Wordle Helper
Wordle puzzle helper

This will help you to close in on an answer. It will remove past words using the file `pastWords.txt`, so
keep this up-to-date. You also have to provide it with information about the guesses you have made.

Change `invalidLetters` to contain letters not in the answer.

Change `correctLetters` to match the correct letters in the correct position.
This must be 5 characters long. For any unknown character use an underscore `_`.

Change `incorrectLetters` to match letters which are in the answer but in the wrong position.
This can be one or more strings. Each must be 5 characters long. For any unknown character
use an underscore `_`.

All characters must be lowercase.

For example:

```kotlin
    // Letters which are not in the answer
    val invalidLetters = "nif"

    // Letters in the correct place e.g. h_p_y, must be 5 characters long
    val correctLetters = "s___y"

    // Letters in the answer but in the wrong place, must be 5 characters long
    val incorrectLetters = listOf(
        "u_a__"
    )
```

Output:

```
Possible Wordle Answers
=======================
saucy
saury
```