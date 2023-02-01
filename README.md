## LiveBroadcast   [![Build Status](https://travis-ci.org/jwolff52/LiveBroadcast.svg?branch=master)](https://travis-ci.org/jwolff52/LiveBroadcast)

Automatic Broadcast Plugin for CraftBukkit

The "title" is the custom name that appears before each message.

The "timer" is the time in seconds between the appearance of each message.

- "min_time" is the time to use when "max_players"+ players are on the server or when "use_scalable_timer" is set to false  
- "max_time" is the time to use when only one player is on the server (Messages are not sent when the server is empty)  
- "max_players" is the value used to calculate how fast to send messages  
- When "use_scalable_timer" is true message delays are based on the number of people on the server, when it is false min_time is used.

You can have as many messages as you would like.

If you would like to use color-codes use the '&' symbol followed by 0-9 and a-f. For more information on 
color-codes visit, http://www.minecraftwiki.net/wiki/Formatting_codes.

If you use a color-code ensure that the string is surrounded by single-quotes (' ').

EXAMPLE:

INCORRECT:

1: This &1is &fa test of &6the color-codes

CORRECT:

1: 'This &1is &fa test of &6the color-codes'
