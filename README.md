CloudDataStorage
================

Privacy Preserving Cloud Data Upload application

NOTE: I'm not responsible if you lose your data on your computer or your cloud.

PHASE - 1
=========

This application uploads data to Box.com cloud service.
Implements a Watch service to monitor a watch folder for changes. 
Any file created inside the watch folder will be pushed to the cloud.
Directories and file modifications inside the watch folder are ignored.
It will ignore any directories or files existing before the start of watch service.


PHASE - 2
=========

Implemented privacy preserving BP-XOR Erasure Coding scheme.
Reorganized the project into better packages.

TODO - Connect Dropbox cloud service.
TODO - Handle larger files. Cache bytes on the storage disk than in the memory.
