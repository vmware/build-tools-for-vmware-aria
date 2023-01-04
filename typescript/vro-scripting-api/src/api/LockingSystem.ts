namespace vroapi {
    let locks: { lockId: string, owner: string }[] = [];

    /**
     * vRO LockingSystem intrinsic class representation
     */
    export class LockingSystem {
        /**
         * Try to acquire a lock. Returns true if the lock is acquired, false otherwise.
         * @param lockId The lock id (what to lock)
         * @param owner The lock owner (who is locking)
         */
        static lock(lockId: string, owner: string): boolean {
            if (locks.some(x => x.lockId === lockId)) {
                return false;
            }
            locks.push({ lockId, owner });
            return true;
        }

        /**
         * Try to acquire a lock and wait until lock is acquired.
         * @param lockId The lock id (what to lock)
         * @param owner The lock owner (who is locking)
         */
        static lockAndWait(lockId: string, owner: string): void {
            locks.push({ lockId, owner });
        }

        /**
         * Release a lock.
         * @param lockId The lock id (what to unlock)
         * @param owner The lock owner (who is unlocking)
         */
        static unlock(lockId: string, owner: string): void {
            for (let i = locks.length - 1; i >= 0; i--) {
                if (locks[i].lockId === lockId && locks[i].owner === owner) {
                    locks.splice(i, 1);
                }
            }
        }

        /**
         * Release all locks.
         */
        static unlockAll(): void {
            locks = [];
        }

        /**
         * Retrieve all locks.
         */
        static retrieveAll(): string[] {
            return locks.map(x => x.lockId);
        }

    }

    global.LockingSystem = LockingSystem;
}
