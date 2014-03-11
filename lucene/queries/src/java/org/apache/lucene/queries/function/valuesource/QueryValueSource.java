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
name|ReaderUtil
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
name|queries
operator|.
name|function
operator|.
name|docvalues
operator|.
name|FloatDocValues
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
name|*
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
name|MutableValueFloat
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
comment|/**  *<code>QueryValueSource</code> returns the relevance score of the query  */
end_comment
begin_class
DECL|class|QueryValueSource
specifier|public
class|class
name|QueryValueSource
extends|extends
name|ValueSource
block|{
DECL|field|q
specifier|final
name|Query
name|q
decl_stmt|;
DECL|field|defVal
specifier|final
name|float
name|defVal
decl_stmt|;
DECL|method|QueryValueSource
specifier|public
name|QueryValueSource
parameter_list|(
name|Query
name|q
parameter_list|,
name|float
name|defVal
parameter_list|)
block|{
name|this
operator|.
name|q
operator|=
name|q
expr_stmt|;
name|this
operator|.
name|defVal
operator|=
name|defVal
expr_stmt|;
block|}
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|q
return|;
block|}
DECL|method|getDefaultValue
specifier|public
name|float
name|getDefaultValue
parameter_list|()
block|{
return|return
name|defVal
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
literal|"query("
operator|+
name|q
operator|+
literal|",def="
operator|+
name|defVal
operator|+
literal|")"
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
name|fcontext
parameter_list|,
name|AtomicReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|QueryDocValues
argument_list|(
name|this
argument_list|,
name|readerContext
argument_list|,
name|fcontext
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
name|q
operator|.
name|hashCode
argument_list|()
operator|*
literal|29
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
name|QueryValueSource
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
name|QueryValueSource
name|other
init|=
operator|(
name|QueryValueSource
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|q
operator|.
name|equals
argument_list|(
name|other
operator|.
name|q
argument_list|)
operator|&&
name|this
operator|.
name|defVal
operator|==
name|other
operator|.
name|defVal
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
name|Weight
name|w
init|=
name|searcher
operator|.
name|createNormalizedWeight
argument_list|(
name|q
argument_list|)
decl_stmt|;
name|context
operator|.
name|put
argument_list|(
name|this
argument_list|,
name|w
argument_list|)
expr_stmt|;
block|}
block|}
end_class
begin_class
DECL|class|QueryDocValues
class|class
name|QueryDocValues
extends|extends
name|FloatDocValues
block|{
DECL|field|readerContext
specifier|final
name|AtomicReaderContext
name|readerContext
decl_stmt|;
DECL|field|acceptDocs
specifier|final
name|Bits
name|acceptDocs
decl_stmt|;
DECL|field|weight
specifier|final
name|Weight
name|weight
decl_stmt|;
DECL|field|defVal
specifier|final
name|float
name|defVal
decl_stmt|;
DECL|field|fcontext
specifier|final
name|Map
name|fcontext
decl_stmt|;
DECL|field|q
specifier|final
name|Query
name|q
decl_stmt|;
DECL|field|scorer
name|Scorer
name|scorer
decl_stmt|;
DECL|field|scorerDoc
name|int
name|scorerDoc
decl_stmt|;
comment|// the document the scorer is on
DECL|field|noMatches
name|boolean
name|noMatches
init|=
literal|false
decl_stmt|;
comment|// the last document requested... start off with high value
comment|// to trigger a scorer reset on first access.
DECL|field|lastDocRequested
name|int
name|lastDocRequested
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|method|QueryDocValues
specifier|public
name|QueryDocValues
parameter_list|(
name|QueryValueSource
name|vs
parameter_list|,
name|AtomicReaderContext
name|readerContext
parameter_list|,
name|Map
name|fcontext
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|vs
argument_list|)
expr_stmt|;
name|this
operator|.
name|readerContext
operator|=
name|readerContext
expr_stmt|;
name|this
operator|.
name|acceptDocs
operator|=
name|readerContext
operator|.
name|reader
argument_list|()
operator|.
name|getLiveDocs
argument_list|()
expr_stmt|;
name|this
operator|.
name|defVal
operator|=
name|vs
operator|.
name|defVal
expr_stmt|;
name|this
operator|.
name|q
operator|=
name|vs
operator|.
name|q
expr_stmt|;
name|this
operator|.
name|fcontext
operator|=
name|fcontext
expr_stmt|;
name|Weight
name|w
init|=
name|fcontext
operator|==
literal|null
condition|?
literal|null
else|:
operator|(
name|Weight
operator|)
name|fcontext
operator|.
name|get
argument_list|(
name|vs
argument_list|)
decl_stmt|;
if|if
condition|(
name|w
operator|==
literal|null
condition|)
block|{
name|IndexSearcher
name|weightSearcher
decl_stmt|;
if|if
condition|(
name|fcontext
operator|==
literal|null
condition|)
block|{
name|weightSearcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|ReaderUtil
operator|.
name|getTopLevelContext
argument_list|(
name|readerContext
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|weightSearcher
operator|=
operator|(
name|IndexSearcher
operator|)
name|fcontext
operator|.
name|get
argument_list|(
literal|"searcher"
argument_list|)
expr_stmt|;
if|if
condition|(
name|weightSearcher
operator|==
literal|null
condition|)
block|{
name|weightSearcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|ReaderUtil
operator|.
name|getTopLevelContext
argument_list|(
name|readerContext
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|vs
operator|.
name|createWeight
argument_list|(
name|fcontext
argument_list|,
name|weightSearcher
argument_list|)
expr_stmt|;
name|w
operator|=
operator|(
name|Weight
operator|)
name|fcontext
operator|.
name|get
argument_list|(
name|vs
argument_list|)
expr_stmt|;
block|}
name|weight
operator|=
name|w
expr_stmt|;
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
try|try
block|{
if|if
condition|(
name|doc
operator|<
name|lastDocRequested
condition|)
block|{
if|if
condition|(
name|noMatches
condition|)
return|return
name|defVal
return|;
name|scorer
operator|=
name|weight
operator|.
name|scorer
argument_list|(
name|readerContext
argument_list|,
name|acceptDocs
argument_list|)
expr_stmt|;
if|if
condition|(
name|scorer
operator|==
literal|null
condition|)
block|{
name|noMatches
operator|=
literal|true
expr_stmt|;
return|return
name|defVal
return|;
block|}
name|scorerDoc
operator|=
operator|-
literal|1
expr_stmt|;
block|}
name|lastDocRequested
operator|=
name|doc
expr_stmt|;
if|if
condition|(
name|scorerDoc
operator|<
name|doc
condition|)
block|{
name|scorerDoc
operator|=
name|scorer
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|scorerDoc
operator|>
name|doc
condition|)
block|{
comment|// query doesn't match this document... either because we hit the
comment|// end, or because the next doc is after this doc.
return|return
name|defVal
return|;
block|}
comment|// a match!
return|return
name|scorer
operator|.
name|score
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"caught exception in QueryDocVals("
operator|+
name|q
operator|+
literal|") doc="
operator|+
name|doc
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|exists
specifier|public
name|boolean
name|exists
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|doc
operator|<
name|lastDocRequested
condition|)
block|{
if|if
condition|(
name|noMatches
condition|)
return|return
literal|false
return|;
name|scorer
operator|=
name|weight
operator|.
name|scorer
argument_list|(
name|readerContext
argument_list|,
name|acceptDocs
argument_list|)
expr_stmt|;
name|scorerDoc
operator|=
operator|-
literal|1
expr_stmt|;
if|if
condition|(
name|scorer
operator|==
literal|null
condition|)
block|{
name|noMatches
operator|=
literal|true
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
name|lastDocRequested
operator|=
name|doc
expr_stmt|;
if|if
condition|(
name|scorerDoc
operator|<
name|doc
condition|)
block|{
name|scorerDoc
operator|=
name|scorer
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|scorerDoc
operator|>
name|doc
condition|)
block|{
comment|// query doesn't match this document... either because we hit the
comment|// end, or because the next doc is after this doc.
return|return
literal|false
return|;
block|}
comment|// a match!
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"caught exception in QueryDocVals("
operator|+
name|q
operator|+
literal|") doc="
operator|+
name|doc
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|objectVal
specifier|public
name|Object
name|objectVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
try|try
block|{
return|return
name|exists
argument_list|(
name|doc
argument_list|)
condition|?
name|scorer
operator|.
name|score
argument_list|()
else|:
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"caught exception in QueryDocVals("
operator|+
name|q
operator|+
literal|") doc="
operator|+
name|doc
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getValueFiller
specifier|public
name|ValueFiller
name|getValueFiller
parameter_list|()
block|{
comment|//
comment|// TODO: if we want to support more than one value-filler or a value-filler in conjunction with
comment|// the FunctionValues, then members like "scorer" should be per ValueFiller instance.
comment|// Or we can say that the user should just instantiate multiple FunctionValues.
comment|//
return|return
operator|new
name|ValueFiller
argument_list|()
block|{
specifier|private
specifier|final
name|MutableValueFloat
name|mval
init|=
operator|new
name|MutableValueFloat
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
try|try
block|{
if|if
condition|(
name|noMatches
condition|)
block|{
name|mval
operator|.
name|value
operator|=
name|defVal
expr_stmt|;
name|mval
operator|.
name|exists
operator|=
literal|false
expr_stmt|;
return|return;
block|}
name|scorer
operator|=
name|weight
operator|.
name|scorer
argument_list|(
name|readerContext
argument_list|,
name|acceptDocs
argument_list|)
expr_stmt|;
name|scorerDoc
operator|=
operator|-
literal|1
expr_stmt|;
if|if
condition|(
name|scorer
operator|==
literal|null
condition|)
block|{
name|noMatches
operator|=
literal|true
expr_stmt|;
name|mval
operator|.
name|value
operator|=
name|defVal
expr_stmt|;
name|mval
operator|.
name|exists
operator|=
literal|false
expr_stmt|;
return|return;
block|}
name|lastDocRequested
operator|=
name|doc
expr_stmt|;
if|if
condition|(
name|scorerDoc
operator|<
name|doc
condition|)
block|{
name|scorerDoc
operator|=
name|scorer
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|scorerDoc
operator|>
name|doc
condition|)
block|{
comment|// query doesn't match this document... either because we hit the
comment|// end, or because the next doc is after this doc.
name|mval
operator|.
name|value
operator|=
name|defVal
expr_stmt|;
name|mval
operator|.
name|exists
operator|=
literal|false
expr_stmt|;
return|return;
block|}
comment|// a match!
name|mval
operator|.
name|value
operator|=
name|scorer
operator|.
name|score
argument_list|()
expr_stmt|;
name|mval
operator|.
name|exists
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"caught exception in QueryDocVals("
operator|+
name|q
operator|+
literal|") doc="
operator|+
name|doc
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
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
return|return
literal|"query("
operator|+
name|q
operator|+
literal|",def="
operator|+
name|defVal
operator|+
literal|")="
operator|+
name|floatVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
block|}
end_class
end_unit
