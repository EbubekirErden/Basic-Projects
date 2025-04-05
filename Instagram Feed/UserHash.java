public class UserHash {

    private static class UserNode {
        User user;
        UserNode next;

        UserNode(User user) {
            this.user = user;
            this.next = null;
        }
    }

    int tableSize = 101;
    UserNode[] userTable;

    public UserHash() {
        this.userTable = new UserNode[tableSize];
    }

    public UserHash(int tableSize) {
        this.tableSize = tableSize;
        this.userTable = new UserNode[tableSize];
    }

    private int getIndex(User user) {
        if (user == null) return -1;

        int hash = user.hash();
        int index = hash % tableSize;

        if (index < 0) {
            index += tableSize;
        }

        return index;
    }

    void put(User user) {
        int index = getIndex(user);
        UserNode newNode = new UserNode(user);

        if (userTable[index] == null) {
            userTable[index] = newNode;
        } else {
            UserNode current = userTable[index];
            while (current.next != null) {
                if (current.user.userID.equals(user.userID)) {
                    current.user = user;
                    return;
                }
                current = current.next;
            }
            current.next = newNode;
        }
    }

    User get(String userID) {
        int index = getIndex(new User(userID));
        UserNode current = userTable[index];

        while (current != null) {
            if (current.user.userID.equals(userID)) {
                return current.user;
            }
            current = current.next;
        }

        return null;
    }

    boolean remove(User user) {
        int index = getIndex(user);
        UserNode current = userTable[index];
        UserNode prev = null;

        while (current != null) {
            if (current.user.userID.equals(user.userID)) {
                if (prev == null) {
                    userTable[index] = current.next;
                } else {
                    prev.next = current.next;
                }
                return true;
            }

            prev = current;
            current = current.next;
        }
        return false;
    }

    boolean exists(String userID) {
        return get(userID) != null;
    }
}
