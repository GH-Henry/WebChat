<h2 align="center" style="font-size:200%;line-height:2;">CSE 3310 Semester Project</h2>

# Project Description

The semester project is the creation of a multi-user, web-based chat program, called WebChat. It will be
coded in the Java programming language and will provide the user(s) with a browser-based interface.
Given this is a summer course and is over 11 weeks (about 2 and a half months) instead of the usual 14
weeks (about 3 months).

# Authors, Group 1

- Edward Alkire [@EddieIsNotAvailable](https://github.com/EddieIsNotAvailable)
- Henry Chiu [@GH-Henry](https://github.com/GH-Henry)
- Derrick Perry [@d-perry0](https://github.com/d-perry0)
- Januar Soepangat [@JanuarS](https://github.com/JanuarS)

# Roadmap

- [X] Iteration 1
  - Framework of the program is created 

- [X] Iteration 2
  - The program is not usable, but has the overall design implemented and supports the sending of text between all users.

- [X] Iteration 3
  - The program is functional, but not perfect. Code is unit tested. The documentation is partially complete. Additional functionality is added to the program.

- [X] Iteration 4
  - Fully functional program. All requirements implemented and verified. Documentation is complete.
  - Status: Not yet started

# Lessons Learned

In iteration 1 we learned how to create branches and merge branches together using git. We also learned how to connect a functioning HTML page with a Java server via websockets. 

In iteration 2 we learned how to identify and record the functional and non-functional requirements of our application. A mock-up of the user interface was created as well as a context and class diagram to describe the relationship between components of the WebChat application. 

In iteration 3 we learned about unit testing and component testing. We began writing unit tests for various functions in our program. 

Int iteration 4 we learned how the web works and deployed a functioning app to the web. Our app can be found at http://cse3310.org:8080/index.html

# Features
- Typing status:
  - Displays notification of when a user starts typing in the message input bar.
  - Displays for 5 seconds, or until a new status notification replaces it.
- Auto-login:
  - Stores username and password in localStorage, unless user explicitly logs out.
  - Can refresh the page, or open the site in a new tab, and will remain logged in with the last used credentials.
- Notifications:
  - When a message is received while the user is in another tab, will send a message notification.
  - Notifications close automatically when the tab is opened.
- Ignore list:
  - Can add and remove users to your ignore list.
  - Messages from ignored users are not displayed.
  - Typing status of ignored users not displayed.
  - Notifications from ignored users are blocked.
- User activity monitoring
  - Tracks the time since each user was last active

# Usage
- Supported commands:
  - /ignore
    - /ignore add <username> <username1> <username...>
      - To add one or more users to your ignore list
    - /ignore remove <username> <username1> <username...>
      - To remove one or more users to your ignore list
    - /ignore list
      - To list the users on your ignore list
  - /activity
    - To list the time since last activity of all users

# Feature Testing Procedures
-Typing status
  - Have two instances logged in with separate credentials.
  - Expect a typing status message to appear for both users when one types in the message bar.
- Auto-login
  - Login to webchat.
  - Refreshing the page and/or closing and reopening the page.
  - Feature is functional if the user is not prompted to login, and their username appears in the bottom left corner, and the expected username appears with their messages.
- Notifications
  - Have two instances logged in with different credentials.
  - Have one instance with their browser open to a different tab than the webchat app.
  - When the other user sends a message, a notification should appear which includes the senders username, and their message.
  - When both have the webchat application open, no notificatins should appear for either of them when each sends a message.
- Ignore list
  - Have a chat with two or more users, each with multiple messages in the message history.
  - After adding 1 or more users to the ignore list with "/ignore add <user1> <user2> <user ...>" <ins>all</ins> of the messages from <ins>all</ins> of the specified users should disappear from the message history.
  - The ignored usernames should all appear upon usage of "/ignore list"
  - The ignoring user should not receive typing status notifications for the ignored users
  - The ignoring user should open another tab, so that the webchat is not active, and when ignored users send messages the ignoring user should not receive notifications for their messages.
  - After using /ignore remove <user1>, all of the messages for the unignored user should appear in the message history again.
  - Unignored users should not still show in /ignore list.
  - Unignored users typing status and notifications should function normally.
- Activity tracking
  - Using different accounts for each test, with a single monitoring account to view the activity, :
    - Track time from creation of an account.
    - Track time from login with an existing user.
    - Login and track time from when you send a message from it.
    - Login and track time from closing out the page of that account.
  - With the master/monitoring account use the /activity command to view the time since last activity of all users. Check the times against the expected values with the accounts used for each test. Do not use /activity from a testing account during this process, since the command usage is a form of activity.


