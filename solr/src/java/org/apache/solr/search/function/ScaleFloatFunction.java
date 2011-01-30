begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search.function
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|function
package|;
end_package
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
name|search
operator|.
name|IndexSearcher
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_comment
comment|/**  * Scales values to be between min and max.  *<p>This implementation currently traverses all of the source values to obtain  * their min and max.  *<p>This implementation currently cannot distinguish when documents have been  * deleted or documents that have no value, and 0.0 values will be used for  * these cases.  This means that if values are normally all greater than 0.0, one can  * still end up with 0.0 as the min value to map from.  In these cases, an  * appropriate map() function could be used as a workaround to change 0.0  * to a value in the real range.  */
end_comment
begin_class
DECL|class|ScaleFloatFunction
specifier|public
class|class
name|ScaleFloatFunction
extends|extends
name|ValueSource
block|{
DECL|field|source
specifier|protected
specifier|final
name|ValueSource
name|source
decl_stmt|;
DECL|field|min
specifier|protected
specifier|final
name|float
name|min
decl_stmt|;
DECL|field|max
specifier|protected
specifier|final
name|float
name|max
decl_stmt|;
DECL|method|ScaleFloatFunction
specifier|public
name|ScaleFloatFunction
parameter_list|(
name|ValueSource
name|source
parameter_list|,
name|float
name|min
parameter_list|,
name|float
name|max
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|min
operator|=
name|min
expr_stmt|;
name|this
operator|.
name|max
operator|=
name|max
expr_stmt|;
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
literal|"scale("
operator|+
name|source
operator|.
name|description
argument_list|()
operator|+
literal|","
operator|+
name|min
operator|+
literal|","
operator|+
name|max
operator|+
literal|")"
return|;
block|}
DECL|class|ScaleInfo
specifier|private
specifier|static
class|class
name|ScaleInfo
block|{
DECL|field|minVal
name|float
name|minVal
decl_stmt|;
DECL|field|maxVal
name|float
name|maxVal
decl_stmt|;
block|}
DECL|method|createScaleInfo
specifier|private
name|ScaleInfo
name|createScaleInfo
parameter_list|(
name|Map
name|context
parameter_list|,
name|AtomicReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|AtomicReaderContext
index|[]
name|leaves
init|=
name|ReaderUtil
operator|.
name|leaves
argument_list|(
name|ReaderUtil
operator|.
name|getTopLevelContext
argument_list|(
name|readerContext
argument_list|)
argument_list|)
decl_stmt|;
name|float
name|minVal
init|=
name|Float
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
name|float
name|maxVal
init|=
name|Float
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
for|for
control|(
name|AtomicReaderContext
name|leaf
range|:
name|leaves
control|)
block|{
name|int
name|maxDoc
init|=
name|leaf
operator|.
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
name|DocValues
name|vals
init|=
name|source
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|leaf
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|maxDoc
condition|;
name|i
operator|++
control|)
block|{
name|float
name|val
init|=
name|vals
operator|.
name|floatVal
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|Float
operator|.
name|floatToRawIntBits
argument_list|(
name|val
argument_list|)
operator|&
operator|(
literal|0xff
operator|<<
literal|23
operator|)
operator|)
operator|==
literal|0xff
operator|<<
literal|23
condition|)
block|{
comment|// if the exponent in the float is all ones, then this is +Inf, -Inf or NaN
comment|// which don't make sense to factor into the scale function
continue|continue;
block|}
if|if
condition|(
name|val
operator|<
name|minVal
condition|)
block|{
name|minVal
operator|=
name|val
expr_stmt|;
block|}
if|if
condition|(
name|val
operator|>
name|maxVal
condition|)
block|{
name|maxVal
operator|=
name|val
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|minVal
operator|==
name|Float
operator|.
name|POSITIVE_INFINITY
condition|)
block|{
comment|// must have been an empty index
name|minVal
operator|=
name|maxVal
operator|=
literal|0
expr_stmt|;
block|}
name|ScaleInfo
name|scaleInfo
init|=
operator|new
name|ScaleInfo
argument_list|()
decl_stmt|;
name|scaleInfo
operator|.
name|minVal
operator|=
name|minVal
expr_stmt|;
name|scaleInfo
operator|.
name|maxVal
operator|=
name|maxVal
expr_stmt|;
name|context
operator|.
name|put
argument_list|(
name|this
operator|.
name|source
argument_list|,
name|scaleInfo
argument_list|)
expr_stmt|;
return|return
name|scaleInfo
return|;
block|}
annotation|@
name|Override
DECL|method|getValues
specifier|public
name|DocValues
name|getValues
parameter_list|(
name|Map
name|context
parameter_list|,
name|AtomicReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
name|ScaleInfo
name|scaleInfo
init|=
operator|(
name|ScaleInfo
operator|)
name|context
operator|.
name|get
argument_list|(
name|source
argument_list|)
decl_stmt|;
if|if
condition|(
name|scaleInfo
operator|==
literal|null
condition|)
block|{
name|scaleInfo
operator|=
name|createScaleInfo
argument_list|(
name|context
argument_list|,
name|readerContext
argument_list|)
expr_stmt|;
block|}
specifier|final
name|float
name|scale
init|=
operator|(
name|scaleInfo
operator|.
name|maxVal
operator|-
name|scaleInfo
operator|.
name|minVal
operator|==
literal|0
operator|)
condition|?
literal|0
else|:
operator|(
name|max
operator|-
name|min
operator|)
operator|/
operator|(
name|scaleInfo
operator|.
name|maxVal
operator|-
name|scaleInfo
operator|.
name|minVal
operator|)
decl_stmt|;
specifier|final
name|float
name|minSource
init|=
name|scaleInfo
operator|.
name|minVal
decl_stmt|;
specifier|final
name|float
name|maxSource
init|=
name|scaleInfo
operator|.
name|maxVal
decl_stmt|;
specifier|final
name|DocValues
name|vals
init|=
name|source
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|readerContext
argument_list|)
decl_stmt|;
return|return
operator|new
name|DocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|float
name|floatVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|vals
operator|.
name|floatVal
argument_list|(
name|doc
argument_list|)
operator|-
name|minSource
operator|)
operator|*
name|scale
operator|+
name|min
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|intVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|floatVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|longVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|long
operator|)
name|floatVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|doubleVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|double
operator|)
name|floatVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|strVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|Float
operator|.
name|toString
argument_list|(
name|floatVal
argument_list|(
name|doc
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
literal|"scale("
operator|+
name|vals
operator|.
name|toString
argument_list|(
name|doc
argument_list|)
operator|+
literal|",toMin="
operator|+
name|min
operator|+
literal|",toMax="
operator|+
name|max
operator|+
literal|",fromMin="
operator|+
name|minSource
operator|+
literal|",fromMax="
operator|+
name|maxSource
operator|+
literal|")"
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|void
name|createWeight
parameter_list|(
name|Map
name|context
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|source
operator|.
name|createWeight
argument_list|(
name|context
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|h
init|=
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|min
argument_list|)
decl_stmt|;
name|h
operator|=
name|h
operator|*
literal|29
expr_stmt|;
name|h
operator|+=
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|max
argument_list|)
expr_stmt|;
name|h
operator|=
name|h
operator|*
literal|29
expr_stmt|;
name|h
operator|+=
name|source
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|h
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
name|ScaleFloatFunction
operator|.
name|class
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|ScaleFloatFunction
name|other
init|=
operator|(
name|ScaleFloatFunction
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|min
operator|==
name|other
operator|.
name|min
operator|&&
name|this
operator|.
name|max
operator|==
name|other
operator|.
name|max
operator|&&
name|this
operator|.
name|source
operator|.
name|equals
argument_list|(
name|other
operator|.
name|source
argument_list|)
return|;
block|}
block|}
end_class
end_unit
