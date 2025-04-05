import java.util.Arrays;

public class PostHeap {
    Post[] heap;
    int DEFAULTSIZE = 100;
    int currentSize;

    public PostHeap(Post[] posts) {
        if (posts == null || posts.length == 0) {
            this.currentSize = 0;
            this.heap = new Post[1]; // Initialize an empty heap with 1 dummy element
            return;
        }

        this.currentSize = posts.length;
        this.heap = new Post[(currentSize + 1)];
        System.arraycopy(posts, 0, heap, 0, currentSize);

        buildHeap();
    }

    public void insert(Post post) {
        if (post == null) return;
        if (currentSize + 1 >= heap.length) {
            heap = Arrays.copyOf(heap, heap.length * 2);
        }

        int hole = ++currentSize;
        for (heap[0] = post; comparePosts(post, heap[hole/2]) < 0; hole /= 2) {
            heap[hole] = heap[hole/2];
        }
        heap[hole] = post;
    }

    private void buildHeap() {
        for (int i = currentSize / 2; i > 0; i--) {
            percolateDown(i);
        }
    }

    private void percolateDown(int hole) {
        int child;
        Post temp = heap[hole];

        for (; hole * 2 <= currentSize; hole = child) {
            child = hole * 2;
            if (child < currentSize && comparePosts(heap[child + 1], heap[child]) > 0)
                child++;
            if (comparePosts(heap[child], temp) > 0)
                heap[hole] = heap[child];
            else
                break;
        }
        heap[hole] = temp;
    }

    public Post getMax() {
        if (isEmpty()) return null;
        Post max = heap[1];
        heap[1] = heap[currentSize--];

        if (currentSize > 0) percolateDown(1);
        return max;
    }

    public boolean isEmpty() {
        return currentSize == 0;
    }

    private int comparePosts(Post post1, Post post2) {
        if (post1 == null && post2 == null) return 0;
        if (post1 == null) return -1;
        if (post2 == null) return 1;

        int likeComparison = Integer.compare(post1.numLike, post2.numLike);
        return likeComparison != 0 ? likeComparison : post1.postID.compareTo(post2.postID);
    }
}