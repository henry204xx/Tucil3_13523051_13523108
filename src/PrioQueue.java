public class PrioQueue {
    private State[] queue;
    private int size;
    private int capacity;

    public PrioQueue(int capacity) {
        this.capacity = capacity;
        this.queue = new State[capacity];
        this.size = 0;
    }

    public void enqueue(State state) {
        if (size == capacity) {
            resize();
        }
        queue[size] = state;
        size++;
        moveUp(size - 1);
    }


    private void resize() {
        capacity *= 2;
        State[] newQueue = new State[capacity];
        System.arraycopy(queue, 0, newQueue, 0, size);
        queue = newQueue;
    }


    public State dequeue() {
        if (size == 0) {
            throw new IllegalStateException("Queue is empty");
        }
        State root = queue[0];
        queue[0] = queue[size - 1];
        size--;
        moveDown(0);
        return root;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void moveUp(int index) {
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            if (queue[index].getTotalCost() < queue[parentIndex].getTotalCost()) {
                swap(index, parentIndex);
                index = parentIndex;
            } else {
                break;
            }
        }
    }

    private void moveDown(int index) {
        while (index < size) {
            int leftChildIndex = 2 * index + 1;
            int rightChildIndex = 2 * index + 2;
            int smallestIndex = index;

            if (leftChildIndex < size && queue[leftChildIndex].getTotalCost() < queue[smallestIndex].getTotalCost()) {
                smallestIndex = leftChildIndex;
            }
            if (rightChildIndex < size && queue[rightChildIndex].getTotalCost() < queue[smallestIndex].getTotalCost()) {
                smallestIndex = rightChildIndex;
            }
            if (smallestIndex != index) {
                swap(index, smallestIndex);
                index = smallestIndex;
            } else {
                break;
            }
        }
    }

    private void swap(int i, int j) {
        State temp = queue[i];
        queue[i] = queue[j];
        queue[j] = temp;
    }

    // public static void main(String[] args) {
    //     PrioQueue queue = new PrioQueue(10);

    //     // Create dummy states with different costs
    //     State s1 = createDummyStateWithCost(5);
    //     State s2 = createDummyStateWithCost(2);
    //     State s3 = createDummyStateWithCost(8);
    //     State s4 = createDummyStateWithCost(1);
    //     State s5 = createDummyStateWithCost(3);

    //     // Enqueue states
    //     queue.enqueue(s1);
    //     queue.enqueue(s2);
    //     queue.enqueue(s3);
    //     queue.enqueue(s4);
    //     queue.enqueue(s5);

    //     // Dequeue all and print costs (should be in ascending order)
    //     System.out.println("Dequeued in order of cost:");
    //     while (!queue.isEmpty()) {
    //         State s = queue.dequeue();
    //         System.out.println("Cost: " + s.getTotalCost());
    //     }
    // }

    // // Helper method to mock a State with only totalCost
    // private static State createDummyStateWithCost(int cost) {
    //     State s = new State(null, null, "", 'X', 0);
    //     s.setTotalCost(cost);
    //     return s;
    // }
}
