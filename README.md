# rhCOP4520A1

This implementation of the solution divides 10^8 into 8 equal slices and assigns a thread to each chunk to calculate the primes for.

There is a global list of prime numbers used to consolidate all calculated primes. Access to this list is synchronized between threads as to not run into any collisions (output before synchronizing this list was extremely inconsistent).

The least efficient parts of this solution are the synchronization of the main primes list, and the sorting of the main primes list after all calculation of primes is complete. A solution to the sorting problem may be to add a binary search when adding to the main primes list, so sorting is not needed and items can still be inserted to the main primes list in logarithmic time.