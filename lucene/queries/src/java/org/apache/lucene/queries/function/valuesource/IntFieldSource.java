begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.queries.function.valuesource
package|package
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
name|valuesource
package|;
end_package
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
name|ValueSourceScorer
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
name|docvalues
operator|.
name|IntDocValues
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|mutable
operator|.
name|MutableValue
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
name|mutable
operator|.
name|MutableValueInt
import|;
end_import
begin_comment
comment|/**  * Obtains int field values from the {@link org.apache.lucene.search.FieldCache}  * using<code>getInts()</code>  * and makes those values available as other numeric types, casting as needed. *  *  */
end_comment
begin_class
DECL|class|IntFieldSource
specifier|public
class|class
name|IntFieldSource
extends|extends
name|FieldCacheSource
block|{
DECL|field|parser
specifier|final
name|FieldCache
operator|.
name|IntParser
name|parser
decl_stmt|;
DECL|method|IntFieldSource
specifier|public
name|IntFieldSource
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
argument_list|(
name|field
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|IntFieldSource
specifier|public
name|IntFieldSource
parameter_list|(
name|String
name|field
parameter_list|,
name|FieldCache
operator|.
name|IntParser
name|parser
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|this
operator|.
name|parser
operator|=
name|parser
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
literal|"int("
operator|+
name|field
operator|+
literal|')'
return|;
block|}
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
specifier|final
name|FieldCache
operator|.
name|Ints
name|arr
init|=
name|cache
operator|.
name|getInts
argument_list|(
name|readerContext
operator|.
name|reader
argument_list|()
argument_list|,
name|field
argument_list|,
name|parser
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|Bits
name|valid
init|=
name|cache
operator|.
name|getDocsWithField
argument_list|(
name|readerContext
operator|.
name|reader
argument_list|()
argument_list|,
name|field
argument_list|)
decl_stmt|;
return|return
operator|new
name|IntDocValues
argument_list|(
name|this
argument_list|)
block|{
specifier|final
name|MutableValueInt
name|val
init|=
operator|new
name|MutableValueInt
argument_list|()
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
return|return
operator|(
name|float
operator|)
name|arr
operator|.
name|get
argument_list|(
name|doc
argument_list|)
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
name|arr
operator|.
name|get
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
name|arr
operator|.
name|get
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
name|arr
operator|.
name|get
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
name|Integer
operator|.
name|toString
argument_list|(
name|arr
operator|.
name|get
argument_list|(
name|doc
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|objectVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|valid
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|?
name|arr
operator|.
name|get
argument_list|(
name|doc
argument_list|)
else|:
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|exists
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|arr
operator|.
name|get
argument_list|(
name|doc
argument_list|)
operator|!=
literal|0
operator|||
name|valid
operator|.
name|get
argument_list|(
name|doc
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
literal|'='
operator|+
name|intVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ValueSourceScorer
name|getRangeScorer
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|lowerVal
parameter_list|,
name|String
name|upperVal
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|)
block|{
name|int
name|lower
decl_stmt|,
name|upper
decl_stmt|;
comment|// instead of using separate comparison functions, adjust the endpoints.
if|if
condition|(
name|lowerVal
operator|==
literal|null
condition|)
block|{
name|lower
operator|=
name|Integer
operator|.
name|MIN_VALUE
expr_stmt|;
block|}
else|else
block|{
name|lower
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|lowerVal
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|includeLower
operator|&&
name|lower
operator|<
name|Integer
operator|.
name|MAX_VALUE
condition|)
name|lower
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|upperVal
operator|==
literal|null
condition|)
block|{
name|upper
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
else|else
block|{
name|upper
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|upperVal
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|includeUpper
operator|&&
name|upper
operator|>
name|Integer
operator|.
name|MIN_VALUE
condition|)
name|upper
operator|--
expr_stmt|;
block|}
specifier|final
name|int
name|ll
init|=
name|lower
decl_stmt|;
specifier|final
name|int
name|uu
init|=
name|upper
decl_stmt|;
return|return
operator|new
name|ValueSourceScorer
argument_list|(
name|reader
argument_list|,
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matchesValue
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|int
name|val
init|=
name|arr
operator|.
name|get
argument_list|(
name|doc
argument_list|)
decl_stmt|;
comment|// only check for deleted if it's the default value
comment|// if (val==0&& reader.isDeleted(doc)) return false;
return|return
name|val
operator|>=
name|ll
operator|&&
name|val
operator|<=
name|uu
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|ValueFiller
name|getValueFiller
parameter_list|()
block|{
return|return
operator|new
name|ValueFiller
argument_list|()
block|{
specifier|private
specifier|final
name|MutableValueInt
name|mval
init|=
operator|new
name|MutableValueInt
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|MutableValue
name|getValue
parameter_list|()
block|{
return|return
name|mval
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|fillValue
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|mval
operator|.
name|value
operator|=
name|arr
operator|.
name|get
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|mval
operator|.
name|exists
operator|=
name|mval
operator|.
name|value
operator|!=
literal|0
operator|||
name|valid
operator|.
name|get
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
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
operator|.
name|getClass
argument_list|()
operator|!=
name|IntFieldSource
operator|.
name|class
condition|)
return|return
literal|false
return|;
name|IntFieldSource
name|other
init|=
operator|(
name|IntFieldSource
operator|)
name|o
decl_stmt|;
return|return
name|super
operator|.
name|equals
argument_list|(
name|other
argument_list|)
operator|&&
operator|(
name|this
operator|.
name|parser
operator|==
literal|null
condition|?
name|other
operator|.
name|parser
operator|==
literal|null
else|:
name|this
operator|.
name|parser
operator|.
name|getClass
argument_list|()
operator|==
name|other
operator|.
name|parser
operator|.
name|getClass
argument_list|()
operator|)
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
name|int
name|h
init|=
name|parser
operator|==
literal|null
condition|?
name|Integer
operator|.
name|class
operator|.
name|hashCode
argument_list|()
else|:
name|parser
operator|.
name|getClass
argument_list|()
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|h
operator|+=
name|super
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|h
return|;
block|}
block|}
end_class
end_unit
