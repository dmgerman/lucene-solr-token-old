begin_unit
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/** A PriorityQueue maintains a partial ordering of its elements such that the  * least element can always be found in constant time.  Put()'s and pop()'s  * require log(size) time.  *  *<p><b>NOTE</b>: This class pre-allocates a full array of  * length<code>maxSize+1</code>, in {@link #initialize}.   *  */
end_comment
begin_class
DECL|class|PriorityQueue
specifier|public
specifier|abstract
class|class
name|PriorityQueue
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|size
specifier|private
name|int
name|size
decl_stmt|;
DECL|field|maxSize
specifier|private
name|int
name|maxSize
decl_stmt|;
DECL|field|heap
specifier|protected
name|T
index|[]
name|heap
decl_stmt|;
comment|/** Determines the ordering of objects in this priority queue.  Subclasses     must define this one method. */
DECL|method|lessThan
specifier|protected
specifier|abstract
name|boolean
name|lessThan
parameter_list|(
name|T
name|a
parameter_list|,
name|T
name|b
parameter_list|)
function_decl|;
comment|/**    * This method can be overridden by extending classes to return a sentinel    * object which will be used by {@link #initialize(int)} to fill the queue, so    * that the code which uses that queue can always assume it's full and only    * change the top without attempting to insert any new object.<br>    *     * Those sentinel values should always compare worse than any non-sentinel    * value (i.e., {@link #lessThan} should always favor the    * non-sentinel values).<br>    *     * By default, this method returns false, which means the queue will not be    * filled with sentinel values. Otherwise, the value returned will be used to    * pre-populate the queue. Adds sentinel values to the queue.<br>    *     * If this method is extended to return a non-null value, then the following    * usage pattern is recommended:    *     *<pre>    * // extends getSentinelObject() to return a non-null value.    * PriorityQueue<MyObject> pq = new MyQueue<MyObject>(numHits);    * // save the 'top' element, which is guaranteed to not be null.    * MyObject pqTop = pq.top();    *&lt;...&gt;    * // now in order to add a new element, which is 'better' than top (after     * // you've verified it is better), it is as simple as:    * pqTop.change().    * pqTop = pq.updateTop();    *</pre>    *     *<b>NOTE:</b> if this method returns a non-null value, it will be called by    * {@link #initialize(int)} {@link #size()} times, relying on a new object to    * be returned and will not check if it's null again. Therefore you should    * ensure any call to this method creates a new instance and behaves    * consistently, e.g., it cannot return null if it previously returned    * non-null.    *     * @return the sentinel object to use to pre-populate the queue, or null if    *         sentinel objects are not supported.    */
DECL|method|getSentinelObject
specifier|protected
name|T
name|getSentinelObject
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/** Subclass constructors must call this. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|initialize
specifier|protected
specifier|final
name|void
name|initialize
parameter_list|(
name|int
name|maxSize
parameter_list|)
block|{
name|size
operator|=
literal|0
expr_stmt|;
name|int
name|heapSize
decl_stmt|;
if|if
condition|(
literal|0
operator|==
name|maxSize
condition|)
comment|// We allocate 1 extra to avoid if statement in top()
name|heapSize
operator|=
literal|2
expr_stmt|;
else|else
block|{
if|if
condition|(
name|maxSize
operator|==
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
comment|// Don't wrap heapSize to -1, in this case, which
comment|// causes a confusing NegativeArraySizeException.
comment|// Note that very likely this will simply then hit
comment|// an OOME, but at least that's more indicative to
comment|// caller that this values is too big.  We don't +1
comment|// in this case, but it's very unlikely in practice
comment|// one will actually insert this many objects into
comment|// the PQ:
name|heapSize
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
else|else
block|{
comment|// NOTE: we add +1 because all access to heap is
comment|// 1-based not 0-based.  heap[0] is unused.
name|heapSize
operator|=
name|maxSize
operator|+
literal|1
expr_stmt|;
block|}
block|}
name|heap
operator|=
operator|(
name|T
index|[]
operator|)
operator|new
name|Object
index|[
name|heapSize
index|]
expr_stmt|;
comment|// T is unbounded type, so this unchecked cast works always
name|this
operator|.
name|maxSize
operator|=
name|maxSize
expr_stmt|;
comment|// If sentinel objects are supported, populate the queue with them
name|T
name|sentinel
init|=
name|getSentinelObject
argument_list|()
decl_stmt|;
if|if
condition|(
name|sentinel
operator|!=
literal|null
condition|)
block|{
name|heap
index|[
literal|1
index|]
operator|=
name|sentinel
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|2
init|;
name|i
operator|<
name|heap
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|heap
index|[
name|i
index|]
operator|=
name|getSentinelObject
argument_list|()
expr_stmt|;
block|}
name|size
operator|=
name|maxSize
expr_stmt|;
block|}
block|}
comment|/**    * Adds an Object to a PriorityQueue in log(size) time. If one tries to add    * more objects than maxSize from initialize an    * {@link ArrayIndexOutOfBoundsException} is thrown.    *     * @return the new 'top' element in the queue.    */
DECL|method|add
specifier|public
specifier|final
name|T
name|add
parameter_list|(
name|T
name|element
parameter_list|)
block|{
name|size
operator|++
expr_stmt|;
name|heap
index|[
name|size
index|]
operator|=
name|element
expr_stmt|;
name|upHeap
argument_list|()
expr_stmt|;
return|return
name|heap
index|[
literal|1
index|]
return|;
block|}
comment|/**    * Adds an Object to a PriorityQueue in log(size) time.    * It returns the object (if any) that was    * dropped off the heap because it was full. This can be    * the given parameter (in case it is smaller than the    * full heap's minimum, and couldn't be added), or another    * object that was previously the smallest value in the    * heap and now has been replaced by a larger one, or null    * if the queue wasn't yet full with maxSize elements.    */
DECL|method|insertWithOverflow
specifier|public
name|T
name|insertWithOverflow
parameter_list|(
name|T
name|element
parameter_list|)
block|{
if|if
condition|(
name|size
operator|<
name|maxSize
condition|)
block|{
name|add
argument_list|(
name|element
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|size
operator|>
literal|0
operator|&&
operator|!
name|lessThan
argument_list|(
name|element
argument_list|,
name|heap
index|[
literal|1
index|]
argument_list|)
condition|)
block|{
name|T
name|ret
init|=
name|heap
index|[
literal|1
index|]
decl_stmt|;
name|heap
index|[
literal|1
index|]
operator|=
name|element
expr_stmt|;
name|updateTop
argument_list|()
expr_stmt|;
return|return
name|ret
return|;
block|}
else|else
block|{
return|return
name|element
return|;
block|}
block|}
comment|/** Returns the least element of the PriorityQueue in constant time. */
DECL|method|top
specifier|public
specifier|final
name|T
name|top
parameter_list|()
block|{
comment|// We don't need to check size here: if maxSize is 0,
comment|// then heap is length 2 array with both entries null.
comment|// If size is 0 then heap[1] is already null.
return|return
name|heap
index|[
literal|1
index|]
return|;
block|}
comment|/** Removes and returns the least element of the PriorityQueue in log(size)     time. */
DECL|method|pop
specifier|public
specifier|final
name|T
name|pop
parameter_list|()
block|{
if|if
condition|(
name|size
operator|>
literal|0
condition|)
block|{
name|T
name|result
init|=
name|heap
index|[
literal|1
index|]
decl_stmt|;
comment|// save first value
name|heap
index|[
literal|1
index|]
operator|=
name|heap
index|[
name|size
index|]
expr_stmt|;
comment|// move last to first
name|heap
index|[
name|size
index|]
operator|=
literal|null
expr_stmt|;
comment|// permit GC of objects
name|size
operator|--
expr_stmt|;
name|downHeap
argument_list|()
expr_stmt|;
comment|// adjust heap
return|return
name|result
return|;
block|}
else|else
return|return
literal|null
return|;
block|}
comment|/**    * Should be called when the Object at top changes values. Still log(n) worst    * case, but it's at least twice as fast to    *     *<pre>    * pq.top().change();    * pq.updateTop();    *</pre>    *     * instead of    *     *<pre>    * o = pq.pop();    * o.change();    * pq.push(o);    *</pre>    *     * @return the new 'top' element.    */
DECL|method|updateTop
specifier|public
specifier|final
name|T
name|updateTop
parameter_list|()
block|{
name|downHeap
argument_list|()
expr_stmt|;
return|return
name|heap
index|[
literal|1
index|]
return|;
block|}
comment|/** Returns the number of elements currently stored in the PriorityQueue. */
DECL|method|size
specifier|public
specifier|final
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
comment|/** Removes all entries from the PriorityQueue. */
DECL|method|clear
specifier|public
specifier|final
name|void
name|clear
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|size
condition|;
name|i
operator|++
control|)
block|{
name|heap
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
block|}
name|size
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|upHeap
specifier|private
specifier|final
name|void
name|upHeap
parameter_list|()
block|{
name|int
name|i
init|=
name|size
decl_stmt|;
name|T
name|node
init|=
name|heap
index|[
name|i
index|]
decl_stmt|;
comment|// save bottom node
name|int
name|j
init|=
name|i
operator|>>>
literal|1
decl_stmt|;
while|while
condition|(
name|j
operator|>
literal|0
operator|&&
name|lessThan
argument_list|(
name|node
argument_list|,
name|heap
index|[
name|j
index|]
argument_list|)
condition|)
block|{
name|heap
index|[
name|i
index|]
operator|=
name|heap
index|[
name|j
index|]
expr_stmt|;
comment|// shift parents down
name|i
operator|=
name|j
expr_stmt|;
name|j
operator|=
name|j
operator|>>>
literal|1
expr_stmt|;
block|}
name|heap
index|[
name|i
index|]
operator|=
name|node
expr_stmt|;
comment|// install saved node
block|}
DECL|method|downHeap
specifier|private
specifier|final
name|void
name|downHeap
parameter_list|()
block|{
name|int
name|i
init|=
literal|1
decl_stmt|;
name|T
name|node
init|=
name|heap
index|[
name|i
index|]
decl_stmt|;
comment|// save top node
name|int
name|j
init|=
name|i
operator|<<
literal|1
decl_stmt|;
comment|// find smaller child
name|int
name|k
init|=
name|j
operator|+
literal|1
decl_stmt|;
if|if
condition|(
name|k
operator|<=
name|size
operator|&&
name|lessThan
argument_list|(
name|heap
index|[
name|k
index|]
argument_list|,
name|heap
index|[
name|j
index|]
argument_list|)
condition|)
block|{
name|j
operator|=
name|k
expr_stmt|;
block|}
while|while
condition|(
name|j
operator|<=
name|size
operator|&&
name|lessThan
argument_list|(
name|heap
index|[
name|j
index|]
argument_list|,
name|node
argument_list|)
condition|)
block|{
name|heap
index|[
name|i
index|]
operator|=
name|heap
index|[
name|j
index|]
expr_stmt|;
comment|// shift up child
name|i
operator|=
name|j
expr_stmt|;
name|j
operator|=
name|i
operator|<<
literal|1
expr_stmt|;
name|k
operator|=
name|j
operator|+
literal|1
expr_stmt|;
if|if
condition|(
name|k
operator|<=
name|size
operator|&&
name|lessThan
argument_list|(
name|heap
index|[
name|k
index|]
argument_list|,
name|heap
index|[
name|j
index|]
argument_list|)
condition|)
block|{
name|j
operator|=
name|k
expr_stmt|;
block|}
block|}
name|heap
index|[
name|i
index|]
operator|=
name|node
expr_stmt|;
comment|// install saved node
block|}
block|}
end_class
end_unit
