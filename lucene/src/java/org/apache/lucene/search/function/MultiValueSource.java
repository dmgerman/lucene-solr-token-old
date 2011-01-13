begin_unit
begin_package
DECL|package|org.apache.lucene.search.function
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|function
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|index
operator|.
name|IndexReader
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
name|index
operator|.
name|IndexReader
operator|.
name|AtomicReaderContext
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
name|index
operator|.
name|IndexReader
operator|.
name|AtomicReaderContext
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
name|index
operator|.
name|IndexReader
operator|.
name|ReaderContext
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
name|search
operator|.
name|Explanation
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
name|ReaderUtil
import|;
end_import
begin_comment
comment|/** This class wraps another ValueSource, but protects  *  against accidental double RAM usage in FieldCache when  *  a composite reader is passed to {@link #getValues}.  *  *<p><b>NOTE</b>: this class adds a CPU penalty to every  *  lookup, as it must resolve the incoming document to the  *  right sub-reader using a binary search.</p>  *  *  @deprecated (4.0) This class is temporary, to ease the  *  migration to segment-based searching. Please change your  *  code to not pass composite readers to these APIs. */
end_comment
begin_class
annotation|@
name|Deprecated
DECL|class|MultiValueSource
specifier|public
specifier|final
class|class
name|MultiValueSource
extends|extends
name|ValueSource
block|{
DECL|field|other
specifier|final
name|ValueSource
name|other
decl_stmt|;
DECL|method|MultiValueSource
specifier|public
name|MultiValueSource
parameter_list|(
name|ValueSource
name|other
parameter_list|)
block|{
name|this
operator|.
name|other
operator|=
name|other
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getValues
specifier|public
name|DocValues
name|getValues
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Already an atomic reader -- just delegate
return|return
name|other
operator|.
name|getValues
argument_list|(
name|context
argument_list|)
return|;
block|}
DECL|method|getValues
specifier|public
name|DocValues
name|getValues
parameter_list|(
name|ReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|context
operator|.
name|isAtomic
condition|)
block|{
return|return
name|getValues
argument_list|(
operator|(
name|AtomicReaderContext
operator|)
name|context
argument_list|)
return|;
block|}
return|return
operator|new
name|MultiDocValues
argument_list|(
name|ReaderUtil
operator|.
name|leaves
argument_list|(
name|context
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
name|other
operator|.
name|description
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|MultiValueSource
condition|)
block|{
return|return
operator|(
operator|(
name|MultiValueSource
operator|)
name|o
operator|)
operator|.
name|other
operator|.
name|equals
argument_list|(
name|other
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
literal|31
operator|*
name|other
operator|.
name|hashCode
argument_list|()
return|;
block|}
DECL|class|MultiDocValues
specifier|private
specifier|final
class|class
name|MultiDocValues
extends|extends
name|DocValues
block|{
DECL|field|docValues
specifier|final
name|DocValues
index|[]
name|docValues
decl_stmt|;
DECL|field|leaves
specifier|final
name|AtomicReaderContext
index|[]
name|leaves
decl_stmt|;
DECL|method|MultiDocValues
name|MultiDocValues
parameter_list|(
name|AtomicReaderContext
index|[]
name|leaves
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|leaves
operator|=
name|leaves
expr_stmt|;
name|docValues
operator|=
operator|new
name|DocValues
index|[
name|leaves
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|leaves
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|docValues
index|[
name|i
index|]
operator|=
name|other
operator|.
name|getValues
argument_list|(
name|leaves
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|floatVal
specifier|public
name|float
name|floatVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
specifier|final
name|int
name|n
init|=
name|ReaderUtil
operator|.
name|subIndex
argument_list|(
name|doc
argument_list|,
name|leaves
argument_list|)
decl_stmt|;
return|return
name|docValues
index|[
name|n
index|]
operator|.
name|floatVal
argument_list|(
name|doc
operator|-
name|leaves
index|[
name|n
index|]
operator|.
name|docBase
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|intVal
specifier|public
name|int
name|intVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
specifier|final
name|int
name|n
init|=
name|ReaderUtil
operator|.
name|subIndex
argument_list|(
name|doc
argument_list|,
name|leaves
argument_list|)
decl_stmt|;
return|return
name|docValues
index|[
name|n
index|]
operator|.
name|intVal
argument_list|(
name|doc
operator|-
name|leaves
index|[
name|n
index|]
operator|.
name|docBase
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|longVal
specifier|public
name|long
name|longVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
specifier|final
name|int
name|n
init|=
name|ReaderUtil
operator|.
name|subIndex
argument_list|(
name|doc
argument_list|,
name|leaves
argument_list|)
decl_stmt|;
return|return
name|docValues
index|[
name|n
index|]
operator|.
name|longVal
argument_list|(
name|doc
operator|-
name|leaves
index|[
name|n
index|]
operator|.
name|docBase
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doubleVal
specifier|public
name|double
name|doubleVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
specifier|final
name|int
name|n
init|=
name|ReaderUtil
operator|.
name|subIndex
argument_list|(
name|doc
argument_list|,
name|leaves
argument_list|)
decl_stmt|;
return|return
name|docValues
index|[
name|n
index|]
operator|.
name|doubleVal
argument_list|(
name|doc
operator|-
name|leaves
index|[
name|n
index|]
operator|.
name|docBase
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|strVal
specifier|public
name|String
name|strVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
specifier|final
name|int
name|n
init|=
name|ReaderUtil
operator|.
name|subIndex
argument_list|(
name|doc
argument_list|,
name|leaves
argument_list|)
decl_stmt|;
return|return
name|docValues
index|[
name|n
index|]
operator|.
name|strVal
argument_list|(
name|doc
operator|-
name|leaves
index|[
name|n
index|]
operator|.
name|docBase
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
specifier|final
name|int
name|n
init|=
name|ReaderUtil
operator|.
name|subIndex
argument_list|(
name|doc
argument_list|,
name|leaves
argument_list|)
decl_stmt|;
return|return
name|docValues
index|[
name|n
index|]
operator|.
name|toString
argument_list|(
name|doc
operator|-
name|leaves
index|[
name|n
index|]
operator|.
name|docBase
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
specifier|final
name|int
name|n
init|=
name|ReaderUtil
operator|.
name|subIndex
argument_list|(
name|doc
argument_list|,
name|leaves
argument_list|)
decl_stmt|;
return|return
name|docValues
index|[
name|n
index|]
operator|.
name|explain
argument_list|(
name|doc
operator|-
name|leaves
index|[
name|n
index|]
operator|.
name|docBase
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
