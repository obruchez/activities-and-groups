# activities-and-groups

[![Scala CI](https://github.com/obruchez/activities-and-groups/actions/workflows/scala.yml/badge.svg)](https://github.com/obruchez/activities-and-groups/actions/workflows/scala.yml)

Very naive (i.e. brute force) algorithm that plans activities for groups of people, trying to avoid having too many persons doing the same activity at the same time. The solution space has a size of (5!)^8 = ~4e16, which is way to big for a systematic search, so the basic idea is to generate random candidate solutions as fast as possible and to minimize a cost function.

The cost function I'm using is not continuous, so unfortunately I don't think I can use methods such as [LBFGS](https://en.wikipedia.org/wiki/Broyden%E2%80%93Fletcher%E2%80%93Goldfarb%E2%80%93Shanno_algorithm). If the cost function had been continuous, I would have used [Breeze](https://github.com/scalanlp/breeze) to minimize that cost function.

There's probably many efficient solutions to this problem, but I'm a bit out of my field here. And a solution was needed quickly! :)