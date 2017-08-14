# *CP App*

**CP** is an app that aims to provide college students with additional help outside of classes.  Professors and TAs usually have very limited office hours outside of class for very difficult classes, leaving struggling students with very little room to improve.  At the University of Southern California, we have an extensive team of CPs (Course Producers/Student TAs) that offer a lot of help to those struggling in our computer science courses.  However, not every school offers this.  This leaves students to turn to traditional tutoring agencies or traditional tutors, but they can be really expensive.  Additionally, something that is often overlooked in school is other students' willingness to help those who are struggling.  CP aims to address all these issues: We want to give students the quality help they need at low prices, and the way we can try to do this is by connecting those who need help with those who are experts in the same class as the struggling student.

## Developers

Timothy Lew (me@timothylew.com)

## Key Highlights and Features

* User authentication (registration and login) via [Firebase](https://firebase.google.com). Users are sent an email verification upon registration, and must click the link inside the verification email.  Users can also request a password reset email if they ever forget their passwords.

* Tutor matching based on what you need help on and what other users are experts in.

* Private messaging service for tutors you match with.

* Tutor ratings are enabled to help prevent bad tutors from infiltrating our studious haven.

* Profile pictures are supported with [Glide](https://github.com/bumptech/glide) and [CircleImageView](https://github.com/hdodenhof/CircleImageView).

* Database and persistent information storage all handled through [Firebase](https://firebase.google.com).

* Push notifications enabled with [Firebase Cloud Messaging](https://firebase.google.com/docs/cloud-messaging/) and local code.  Notifications are automatically sent when a new tutor relationship/message channel is established through local code, and notifications can be sent manually from the cloud through Firebase Cloud Messaging.

## Walkthrough Videos

<img src='https://github.com/timothylew/CP_Android/blob/master/walkthroughs/Login1.gif' title='Login Walkthrough 1' width='' alt='Login Walkthrough 1' />
<img src='https://github.com/timothylew/CP_Android/blob/master/walkthroughs/Login2.gif' title='Login Walkthrough 2' width='' alt='Login Walkthrough 2' />
<img src='https://github.com/timothylew/CP_Android/blob/master/walkthroughs/Walkthrough%20(Update).gif' title='Overall Walkthrough' width='' alt='Overall Walkthrough' />

All walkthrough GIFs created with [LiceCap](http://www.cockos.com/licecap/).

## Notes about Repo

* All bugs fixed as of now. To report a bug, please email me@timothylew.com


## License

    Copyright 2017

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
