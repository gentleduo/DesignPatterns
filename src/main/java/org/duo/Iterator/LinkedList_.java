package org.duo.Iterator;

/**
 * 相比数组，这个容器不用考虑边界问题，可以动态扩展
 */
class LinkedList_<E> implements Collection_<E> {

    Node head = null;
    Node tail = null;
    //目前容器中有多少个元素
    private int size = 0;

    /**
     * 如果容器中只放了一个元素的话，会出现下面的情况：
     * head == tail == head.next == tail.next == 唯一的对象:o
     * 所以不能单纯以Node.next为不为空来判断集合中是否还有元素，要考虑容器中只有一个元素的特殊情况
     *
     * @param o
     */
    public void add(E o) {
        Node n = new Node(o);
        n.next = null;

        if (head == null) {
            head = n;
            tail = n;
        }

        tail.next = n;
        tail = n;
        size++;
    }

    private class Node<E> {
        private E o;
        Node next;

        public Node(E o) {
            this.o = o;
        }
    }

    public int size() {
        return size;
    }

    @Override
    public Iterator_ iterator() {
        return new LinkListIterator();
    }

    private class LinkListIterator<E> implements Iterator_<E> {

        //        private int currentIndex = 0;
        private Node currentNode = null;

        public LinkListIterator() {
            currentNode = head;
        }

        @Override
        public boolean hasNext() {
//            if (currentIndex >= size) return false;
//            return true;

            if (currentNode == null) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        public E next() {
//            if (currentNode != null) {
//                Node retNode = currentNode;
//                currentNode = currentNode.next;
//                return (E) retNode.o;
//            } else {
//                return null;
//            }
            if (currentNode != null) {
                Node retNode = currentNode;
                // 当节点的next指定的节点就是当前节点时：说明容器中只有一个节点
                if (currentNode.next == currentNode) {
                    currentNode = null;
                } else {
                    currentNode = currentNode.next;
                }
                return (E) retNode.o;
            } else {
                return null;
            }
        }
    }
}
