begin_unit
begin_package
DECL|package|org.apache.lucene.spatial.composite
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|composite
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|LeafReaderContext
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
name|ConstantScoreScorer
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
name|ConstantScoreWeight
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
name|search
operator|.
name|Query
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
name|Scorer
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
name|TwoPhaseIterator
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
name|Weight
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
begin_comment
comment|/**  * A Query that considers an "indexQuery" to have approximate results, and a follow-on  * {@link ValueSource}/{@link FunctionValues#boolVal(int)} is called to verify each hit  * from {@link TwoPhaseIterator#matches()}.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|CompositeVerifyQuery
specifier|public
class|class
name|CompositeVerifyQuery
extends|extends
name|Query
block|{
DECL|field|indexQuery
specifier|final
name|Query
name|indexQuery
decl_stmt|;
comment|//approximation (matches more than needed)
DECL|field|predicateValueSource
specifier|final
name|ValueSource
name|predicateValueSource
decl_stmt|;
comment|//we call boolVal(doc)
DECL|method|CompositeVerifyQuery
specifier|public
name|CompositeVerifyQuery
parameter_list|(
name|Query
name|indexQuery
parameter_list|,
name|ValueSource
name|predicateValueSource
parameter_list|)
block|{
name|this
operator|.
name|indexQuery
operator|=
name|indexQuery
expr_stmt|;
name|this
operator|.
name|predicateValueSource
operator|=
name|predicateValueSource
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Query
name|rewritten
init|=
name|indexQuery
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|rewritten
operator|!=
name|indexQuery
condition|)
block|{
return|return
operator|new
name|CompositeVerifyQuery
argument_list|(
name|rewritten
argument_list|,
name|predicateValueSource
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
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
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
return|return
literal|false
return|;
name|CompositeVerifyQuery
name|that
init|=
operator|(
name|CompositeVerifyQuery
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|indexQuery
operator|.
name|equals
argument_list|(
name|that
operator|.
name|indexQuery
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|predicateValueSource
operator|.
name|equals
argument_list|(
name|that
operator|.
name|predicateValueSource
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
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
name|result
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|indexQuery
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|predicateValueSource
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
comment|//TODO verify this looks good
return|return
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"("
operator|+
name|indexQuery
operator|.
name|toString
argument_list|(
name|field
argument_list|)
operator|+
literal|", "
operator|+
name|predicateValueSource
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Weight
name|indexQueryWeight
init|=
name|indexQuery
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|//scores aren't unsupported
specifier|final
name|Map
name|valueSourceContext
init|=
name|ValueSource
operator|.
name|newContext
argument_list|(
name|searcher
argument_list|)
decl_stmt|;
return|return
operator|new
name|ConstantScoreWeight
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Scorer
name|indexQueryScorer
init|=
name|indexQueryWeight
operator|.
name|scorer
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexQueryScorer
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|FunctionValues
name|predFuncValues
init|=
name|predicateValueSource
operator|.
name|getValues
argument_list|(
name|valueSourceContext
argument_list|,
name|context
argument_list|)
decl_stmt|;
specifier|final
name|TwoPhaseIterator
name|twoPhaseIterator
init|=
operator|new
name|TwoPhaseIterator
argument_list|(
name|indexQueryScorer
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|predFuncValues
operator|.
name|boolVal
argument_list|(
name|indexQueryScorer
operator|.
name|docID
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|matchCost
parameter_list|()
block|{
return|return
literal|100
return|;
comment|// TODO: use cost of predFuncValues.boolVal()
block|}
block|}
decl_stmt|;
return|return
operator|new
name|ConstantScoreScorer
argument_list|(
name|this
argument_list|,
name|score
argument_list|()
argument_list|,
name|twoPhaseIterator
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
