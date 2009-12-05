begin_unit
begin_package
DECL|package|org.apache.lucene.search.similar
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|similar
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
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|analysis
operator|.
name|standard
operator|.
name|StandardAnalyzer
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|IndexWriter
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
name|IndexWriter
operator|.
name|MaxFieldLength
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
name|BooleanClause
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
name|BooleanQuery
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
name|TermQuery
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
name|store
operator|.
name|RAMDirectory
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
begin_class
DECL|class|TestMoreLikeThis
specifier|public
class|class
name|TestMoreLikeThis
extends|extends
name|LuceneTestCase
block|{
DECL|field|directory
specifier|private
name|RAMDirectory
name|directory
decl_stmt|;
DECL|field|reader
specifier|private
name|IndexReader
name|reader
decl_stmt|;
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|directory
operator|=
operator|new
name|RAMDirectory
argument_list|()
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|(
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
argument_list|,
literal|true
argument_list|,
name|MaxFieldLength
operator|.
name|UNLIMITED
argument_list|)
decl_stmt|;
comment|// Add series of docs with specific information for MoreLikeThis
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"lucene"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"lucene release"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|directory
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|addDoc
specifier|private
name|void
name|addDoc
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|String
name|text
parameter_list|)
throws|throws
name|IOException
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"text"
argument_list|,
name|text
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
DECL|method|testBoostFactor
specifier|public
name|void
name|testBoostFactor
parameter_list|()
throws|throws
name|Throwable
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|originalValues
init|=
name|getOriginalValues
argument_list|()
decl_stmt|;
name|MoreLikeThis
name|mlt
init|=
operator|new
name|MoreLikeThis
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|mlt
operator|.
name|setMinDocFreq
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setMinTermFreq
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setMinWordLen
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setFieldNames
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"text"
block|}
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setBoost
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// this mean that every term boost factor will be multiplied by this
comment|// number
name|float
name|boostFactor
init|=
literal|5
decl_stmt|;
name|mlt
operator|.
name|setBoostFactor
argument_list|(
name|boostFactor
argument_list|)
expr_stmt|;
name|BooleanQuery
name|query
init|=
operator|(
name|BooleanQuery
operator|)
name|mlt
operator|.
name|like
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"lucene release"
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|BooleanClause
argument_list|>
name|clauses
init|=
name|query
operator|.
name|clauses
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Expected "
operator|+
name|originalValues
operator|.
name|size
argument_list|()
operator|+
literal|" clauses."
argument_list|,
name|originalValues
operator|.
name|size
argument_list|()
argument_list|,
name|clauses
operator|.
name|size
argument_list|()
argument_list|)
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
name|clauses
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|BooleanClause
name|clause
init|=
name|clauses
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|TermQuery
name|tq
init|=
operator|(
name|TermQuery
operator|)
name|clause
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|Float
name|termBoost
init|=
operator|(
name|Float
operator|)
name|originalValues
operator|.
name|get
argument_list|(
name|tq
operator|.
name|getTerm
argument_list|()
operator|.
name|text
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Expected term "
operator|+
name|tq
operator|.
name|getTerm
argument_list|()
operator|.
name|text
argument_list|()
argument_list|,
name|termBoost
argument_list|)
expr_stmt|;
name|float
name|totalBoost
init|=
name|termBoost
operator|.
name|floatValue
argument_list|()
operator|*
name|boostFactor
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Expected boost of "
operator|+
name|totalBoost
operator|+
literal|" for term '"
operator|+
name|tq
operator|.
name|getTerm
argument_list|()
operator|.
name|text
argument_list|()
operator|+
literal|"' got "
operator|+
name|tq
operator|.
name|getBoost
argument_list|()
argument_list|,
name|totalBoost
argument_list|,
name|tq
operator|.
name|getBoost
argument_list|()
argument_list|,
literal|0.0001
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getOriginalValues
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|getOriginalValues
parameter_list|()
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|originalValues
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
argument_list|()
decl_stmt|;
name|MoreLikeThis
name|mlt
init|=
operator|new
name|MoreLikeThis
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|mlt
operator|.
name|setMinDocFreq
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setMinTermFreq
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setMinWordLen
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setFieldNames
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"text"
block|}
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setBoost
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|BooleanQuery
name|query
init|=
operator|(
name|BooleanQuery
operator|)
name|mlt
operator|.
name|like
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"lucene release"
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|BooleanClause
argument_list|>
name|clauses
init|=
name|query
operator|.
name|clauses
argument_list|()
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
name|clauses
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|BooleanClause
name|clause
init|=
name|clauses
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|TermQuery
name|tq
init|=
operator|(
name|TermQuery
operator|)
name|clause
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|originalValues
operator|.
name|put
argument_list|(
name|tq
operator|.
name|getTerm
argument_list|()
operator|.
name|text
argument_list|()
argument_list|,
name|Float
operator|.
name|valueOf
argument_list|(
name|tq
operator|.
name|getBoost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|originalValues
return|;
block|}
block|}
end_class
end_unit
