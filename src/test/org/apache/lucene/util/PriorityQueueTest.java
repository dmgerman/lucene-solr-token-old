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
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import
begin_class
DECL|class|PriorityQueueTest
class|class
name|PriorityQueueTest
block|{
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|test
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
block|}
DECL|class|IntegerQueue
specifier|private
specifier|static
class|class
name|IntegerQueue
extends|extends
name|PriorityQueue
block|{
DECL|method|IntegerQueue
specifier|public
name|IntegerQueue
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|initialize
argument_list|(
name|count
argument_list|)
expr_stmt|;
block|}
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|Object
name|a
parameter_list|,
name|Object
name|b
parameter_list|)
block|{
return|return
operator|(
operator|(
name|Integer
operator|)
name|a
operator|)
operator|.
name|intValue
argument_list|()
operator|<
operator|(
operator|(
name|Integer
operator|)
name|b
operator|)
operator|.
name|intValue
argument_list|()
return|;
block|}
block|}
DECL|method|test
specifier|public
specifier|static
name|void
name|test
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|PriorityQueue
name|pq
init|=
operator|new
name|IntegerQueue
argument_list|(
name|count
argument_list|)
decl_stmt|;
name|Random
name|gen
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|int
name|i
decl_stmt|;
name|Date
name|start
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|pq
operator|.
name|put
argument_list|(
operator|new
name|Integer
argument_list|(
name|gen
operator|.
name|nextInt
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Date
name|end
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
operator|(
call|(
name|float
call|)
argument_list|(
name|end
operator|.
name|getTime
argument_list|()
operator|-
name|start
operator|.
name|getTime
argument_list|()
argument_list|)
operator|/
name|count
operator|)
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" microseconds/put"
argument_list|)
expr_stmt|;
name|start
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
name|int
name|last
init|=
name|Integer
operator|.
name|MIN_VALUE
decl_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|Integer
name|next
init|=
operator|(
name|Integer
operator|)
name|pq
operator|.
name|pop
argument_list|()
decl_stmt|;
if|if
condition|(
name|next
operator|.
name|intValue
argument_list|()
operator|<=
name|last
condition|)
throw|throw
operator|new
name|Error
argument_list|(
literal|"out of order"
argument_list|)
throw|;
name|last
operator|=
name|next
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
name|end
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
operator|(
call|(
name|float
call|)
argument_list|(
name|end
operator|.
name|getTime
argument_list|()
operator|-
name|start
operator|.
name|getTime
argument_list|()
argument_list|)
operator|/
name|count
operator|)
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" microseconds/pop"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
