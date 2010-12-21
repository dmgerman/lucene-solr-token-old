begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.common.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|IteratorChain
import|;
end_import
begin_class
DECL|class|IteratorChainTest
specifier|public
class|class
name|IteratorChainTest
extends|extends
name|LuceneTestCase
block|{
DECL|method|makeIterator
specifier|private
name|Iterator
argument_list|<
name|String
argument_list|>
name|makeIterator
parameter_list|(
name|String
name|marker
parameter_list|,
name|int
name|howMany
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|c
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|howMany
condition|;
name|i
operator|++
control|)
block|{
name|c
operator|.
name|add
argument_list|(
name|marker
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|c
operator|.
name|iterator
argument_list|()
return|;
block|}
DECL|method|testNoIterator
specifier|public
name|void
name|testNoIterator
parameter_list|()
block|{
specifier|final
name|IteratorChain
argument_list|<
name|String
argument_list|>
name|c
init|=
operator|new
name|IteratorChain
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
literal|"Empty IteratorChain.hastNext() is false"
argument_list|,
name|c
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|getString
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testCallNextTooEarly
specifier|public
name|void
name|testCallNextTooEarly
parameter_list|()
block|{
specifier|final
name|IteratorChain
argument_list|<
name|String
argument_list|>
name|c
init|=
operator|new
name|IteratorChain
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|c
operator|.
name|addIterator
argument_list|(
name|makeIterator
argument_list|(
literal|"a"
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|c
operator|.
name|next
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Calling next() before hasNext() should throw RuntimeException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|asExpected
parameter_list|)
block|{
comment|// we're fine
block|}
block|}
DECL|method|testCallAddTooLate
specifier|public
name|void
name|testCallAddTooLate
parameter_list|()
block|{
specifier|final
name|IteratorChain
argument_list|<
name|String
argument_list|>
name|c
init|=
operator|new
name|IteratorChain
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|c
operator|.
name|hasNext
argument_list|()
expr_stmt|;
try|try
block|{
name|c
operator|.
name|addIterator
argument_list|(
name|makeIterator
argument_list|(
literal|"a"
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Calling addIterator after hasNext() should throw RuntimeException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|asExpected
parameter_list|)
block|{
comment|// we're fine
block|}
block|}
DECL|method|testRemove
specifier|public
name|void
name|testRemove
parameter_list|()
block|{
specifier|final
name|IteratorChain
argument_list|<
name|String
argument_list|>
name|c
init|=
operator|new
name|IteratorChain
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
name|c
operator|.
name|remove
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Calling remove should throw UnsupportedOperationException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|asExpected
parameter_list|)
block|{
comment|// we're fine
block|}
block|}
DECL|method|testOneIterator
specifier|public
name|void
name|testOneIterator
parameter_list|()
block|{
specifier|final
name|IteratorChain
argument_list|<
name|String
argument_list|>
name|c
init|=
operator|new
name|IteratorChain
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|c
operator|.
name|addIterator
argument_list|(
name|makeIterator
argument_list|(
literal|"a"
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a1a2a3"
argument_list|,
name|getString
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testTwoIterators
specifier|public
name|void
name|testTwoIterators
parameter_list|()
block|{
specifier|final
name|IteratorChain
argument_list|<
name|String
argument_list|>
name|c
init|=
operator|new
name|IteratorChain
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|c
operator|.
name|addIterator
argument_list|(
name|makeIterator
argument_list|(
literal|"a"
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|.
name|addIterator
argument_list|(
name|makeIterator
argument_list|(
literal|"b"
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a1a2a3b1b2"
argument_list|,
name|getString
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testEmptyIteratorsInTheMiddle
specifier|public
name|void
name|testEmptyIteratorsInTheMiddle
parameter_list|()
block|{
specifier|final
name|IteratorChain
argument_list|<
name|String
argument_list|>
name|c
init|=
operator|new
name|IteratorChain
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|c
operator|.
name|addIterator
argument_list|(
name|makeIterator
argument_list|(
literal|"a"
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|.
name|addIterator
argument_list|(
name|makeIterator
argument_list|(
literal|"b"
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|.
name|addIterator
argument_list|(
name|makeIterator
argument_list|(
literal|"c"
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a1a2a3c1"
argument_list|,
name|getString
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** dump the contents of it to a String */
DECL|method|getString
specifier|private
name|String
name|getString
parameter_list|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|""
argument_list|)
expr_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
