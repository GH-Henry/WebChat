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
