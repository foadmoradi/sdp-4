Development
===========

This document aims to do two things. Hopefully it should provide...

* ...a guide to collaborating with the git version control system.
* ...some pointers to do with the other aspects of development.

Collaborating with Git
----------------------

### Introduction

Git may be slightly different to any version control system you have used
before. If you are familiar with SVN, CVS or other *centralised* version control
systems then some of the paradigms may take some getting used to. If you have
used other *distributed* version control systems in the past then the learning
curve should be small and shallow.

### Tutorials

There are many git tutorials online to get you familiar with the basic commands.
The two that I would recommend are:

* Git Immersion - [http://library.edgecase.com/git_immersion/]()

  A great beginner's tutorial that steps through everything you need to know and
  more.

* Git Reference - [http://gitref.org/]()

  This is a good reference tutorial that links well with the official git
  documentation and a free book written by the same author.

### Why git?

I believe that using git is the correct choice for this project for a few
reasons (there is no learning value in the next section so feel free to skip
over it if you would like):

1. It's distributed.
   If you aren't familiar with version control lingo this basically means that
   when you clone the source code to your machine you get a complete copy of
   the entire project and its history. This in turn has a number of advantages
   that make it useful to a project like this:

   * No dependency on a network connection.
     Contrary to what I just said, this point probably doesn't have any
     beneficial advantages this project (unless you guys happen to work on a
     train a lot). This means you can commit, check this history and revert
     changes without requesting or recieving any data over a network connection.
   
   * It's extremely fast.
     Again, I'm sorry - this still isn't a specific point that is a benefit
     to this project (I promise I'll make it up on the next one). However,
     another general advantage of using distributed version control is that, as
     discussed above, git doesn't require the network to do many basic commands.
     This (along with some horrific C hacks and design decisions) allows it to
     be crazy fast.

   * Complete development isolation.
     Okay, this is the advantage I promised - complete development isolation.
     What this means is that you can do whatever you like to your local branch
     without affecting development being done on the main branch. This allows
     you to try some changes out locally without you having to worry about your
     changes being seen by anyone else. When thay are ready it's dead easy to
     throw a whole branch of development away or merge it into the stable, main
     branch. Your version control system shouldn't make you hesitant to try
     stuff out with the fear that it might go wrong. Trying changes out and
     being able to revert them is a lot of what version control is about in the
     first place!

2. Github.com
   Here is the part of the prose where I sound like more of a fanboy than normal
   and say that Github is the best thing about using git. Seriously. It's
   incredible. They've taken the classically clunky and troublesome problem
   of sharing distributed version control code and dressed it up with a great
   interface, some brilliant features and a tonne of useful functionality.

Development with Git
--------------------

### Git Commit Messages

Having useful and meaningful commit messages is essential to having
a manageable project history. It allows for everyone to see at a glance what
work is being done by other people on the project.

It is also a great tool to check what work *you* have done over the past two
weeks for writing your personal reports. You can do this with the command:

    git log --author="Chris Brown" --after="14 days"

This command will get all of your commits from the last 2 weeks so you can get
a detailed list of everything you have done.

A good format to use with commit messages is the one that Tim Pope describes here:
[http://tbaggery.com/2008/04/19/a-note-about-git-commit-messages.html]().

### Normal Git Workflow

Your master branch should always be stable and so you should avoid doing any
work in that branch directly. You should create topic branches to keep
relevant work in the same place.

Let's say we want to work on the first issue in the issue tracker...

1. First we should update our code and create a new branch:

    `git pull origin master`

    `git checkout master`
	
	`git branch 1-blocking-shoot`

	`git checkout 1-blocking-shoot`

   Notice that we prepend the name of the branch with the issue number to keep
   a reference.

2. Now we can work-add-commit on the branch to fix the issue.

3. On the final commit if you include the phrase "Fixes #1." (where 1 is the
   issue number) then Github will automatically close the issue and make a link
   to your commit in the comments.

4. Push your changes to Github.

    `git push origin 1-blocking-shoot`

5. Now if you go to Github and open a pull request from your branch to the
   master branch then someone will review the changes and pull them into the
   master for everyone else to get!
