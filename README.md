# BookMe-Android

Book Me is a virtual assistant which books appointments for you! The user interfaces with it by tapping the microphone icon and speaking to it, an example being "Can I get a reservation for three at Moxy's between 7 to 9 PM". Book Me will then call Moxy's for you to reserve a table in the requested time range, then will notify you if the booking was successful and for which specific time it was done.

For voice recognition on the Android application, Book Me uses the Nuance API's Natural Language Understanding to understand the requested restaurant, time range and number of people needed at the table. Book Me will then send that data to a backend server written in Python, which will then make use of the Twilio API to handle calling. However, the Twilio implementation is still incomplete.
