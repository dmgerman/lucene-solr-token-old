begin_unit
begin_package
DECL|package|org.apache.lucene.spatial.bbox
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|bbox
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Rectangle
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
name|AtomicReader
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
name|queries
operator|.
name|function
operator|.
name|FunctionValues
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|search
operator|.
name|FieldCache
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
name|Bits
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
comment|/**  * An implementation of the Lucene ValueSource model to support spatial relevance ranking.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|BBoxSimilarityValueSource
specifier|public
class|class
name|BBoxSimilarityValueSource
extends|extends
name|ValueSource
block|{
DECL|field|strategy
specifier|private
specifier|final
name|BBoxStrategy
name|strategy
decl_stmt|;
DECL|field|similarity
specifier|private
specifier|final
name|BBoxSimilarity
name|similarity
decl_stmt|;
DECL|method|BBoxSimilarityValueSource
specifier|public
name|BBoxSimilarityValueSource
parameter_list|(
name|BBoxStrategy
name|strategy
parameter_list|,
name|BBoxSimilarity
name|similarity
parameter_list|)
block|{
name|this
operator|.
name|strategy
operator|=
name|strategy
expr_stmt|;
name|this
operator|.
name|similarity
operator|=
name|similarity
expr_stmt|;
block|}
comment|/**    * Returns the ValueSource description.    *    * @return the description    */
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"BBoxSimilarityValueSource("
operator|+
name|similarity
operator|+
literal|")"
return|;
block|}
comment|/**    * Returns the DocValues used by the function query.    *    * @param readerContext the AtomicReaderContext which holds an AtomicReader    * @return the values    */
annotation|@
name|Override
DECL|method|getValues
specifier|public
name|FunctionValues
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
name|AtomicReader
name|reader
init|=
name|readerContext
operator|.
name|reader
argument_list|()
decl_stmt|;
specifier|final
name|FieldCache
operator|.
name|Doubles
name|minX
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getDoubles
argument_list|(
name|reader
argument_list|,
name|strategy
operator|.
name|field_minX
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|FieldCache
operator|.
name|Doubles
name|minY
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getDoubles
argument_list|(
name|reader
argument_list|,
name|strategy
operator|.
name|field_minY
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|FieldCache
operator|.
name|Doubles
name|maxX
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getDoubles
argument_list|(
name|reader
argument_list|,
name|strategy
operator|.
name|field_maxX
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|FieldCache
operator|.
name|Doubles
name|maxY
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getDoubles
argument_list|(
name|reader
argument_list|,
name|strategy
operator|.
name|field_maxY
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|Bits
name|validMinX
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getDocsWithField
argument_list|(
name|reader
argument_list|,
name|strategy
operator|.
name|field_minX
argument_list|)
decl_stmt|;
specifier|final
name|Bits
name|validMaxX
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getDocsWithField
argument_list|(
name|reader
argument_list|,
name|strategy
operator|.
name|field_maxX
argument_list|)
decl_stmt|;
return|return
operator|new
name|FunctionValues
argument_list|()
block|{
comment|//reused
name|Rectangle
name|rect
init|=
name|strategy
operator|.
name|getSpatialContext
argument_list|()
operator|.
name|makeRectangle
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
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
name|double
name|minXVal
init|=
name|minX
operator|.
name|get
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|double
name|maxXVal
init|=
name|maxX
operator|.
name|get
argument_list|(
name|doc
argument_list|)
decl_stmt|;
comment|// make sure it has minX and area
if|if
condition|(
operator|(
name|minXVal
operator|!=
literal|0
operator|||
name|validMinX
operator|.
name|get
argument_list|(
name|doc
argument_list|)
operator|)
operator|&&
operator|(
name|maxXVal
operator|!=
literal|0
operator|||
name|validMaxX
operator|.
name|get
argument_list|(
name|doc
argument_list|)
operator|)
condition|)
block|{
name|rect
operator|.
name|reset
argument_list|(
name|minXVal
argument_list|,
name|maxXVal
argument_list|,
name|minY
operator|.
name|get
argument_list|(
name|doc
argument_list|)
argument_list|,
name|maxY
operator|.
name|get
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|(
name|float
operator|)
name|similarity
operator|.
name|score
argument_list|(
name|rect
argument_list|,
literal|null
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|(
name|float
operator|)
name|similarity
operator|.
name|score
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Explanation
name|explain
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
comment|// make sure it has minX and area
if|if
condition|(
name|validMinX
operator|.
name|get
argument_list|(
name|doc
argument_list|)
operator|&&
name|validMaxX
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|rect
operator|.
name|reset
argument_list|(
name|minX
operator|.
name|get
argument_list|(
name|doc
argument_list|)
argument_list|,
name|maxX
operator|.
name|get
argument_list|(
name|doc
argument_list|)
argument_list|,
name|minY
operator|.
name|get
argument_list|(
name|doc
argument_list|)
argument_list|,
name|maxY
operator|.
name|get
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|Explanation
name|exp
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
name|similarity
operator|.
name|score
argument_list|(
name|rect
argument_list|,
name|exp
argument_list|)
expr_stmt|;
return|return
name|exp
return|;
block|}
return|return
operator|new
name|Explanation
argument_list|(
literal|0
argument_list|,
literal|"No BBox"
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
name|description
argument_list|()
operator|+
literal|"="
operator|+
name|floatVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**    * Determines if this ValueSource is equal to another.    *    * @param o the ValueSource to compare    * @return<code>true</code> if the two objects are based upon the same query envelope    */
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
operator|.
name|getClass
argument_list|()
operator|!=
name|BBoxSimilarityValueSource
operator|.
name|class
condition|)
block|{
return|return
literal|false
return|;
block|}
name|BBoxSimilarityValueSource
name|other
init|=
operator|(
name|BBoxSimilarityValueSource
operator|)
name|o
decl_stmt|;
return|return
name|similarity
operator|.
name|equals
argument_list|(
name|other
operator|.
name|similarity
argument_list|)
return|;
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
name|BBoxSimilarityValueSource
operator|.
name|class
operator|.
name|hashCode
argument_list|()
operator|+
name|similarity
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class
end_unit
