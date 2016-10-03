# Introduction to clojure-challenge

The aim of this system is to calculate the reward score for customers confirmed invitation following this path:

The definition of a confirmed invitation is one where another invitation's invitee invited someone.

The inviter gets (1/2)^k points for each confirmed invitation, where k is the level of the invitation: level 0 (people directly invited) yields 1 point, level 1 (people invited by someone invited by the original customer) gives 1/2 points, level 2 invitations (people invited by someone on level 1) awards 1/4 points and so on. 

Only the first invitation counts: multiple invites sent to the same person don't produce any further points, even if they come from different inviters.