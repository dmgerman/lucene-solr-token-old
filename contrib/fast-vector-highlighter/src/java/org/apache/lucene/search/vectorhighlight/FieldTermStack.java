begin_unit
begin_package
DECL|package|org.apache.lucene.search.vectorhighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|vectorhighlight
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
name|util
operator|.
name|Collections
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|Analyzer
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
name|WhitespaceAnalyzer
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
name|document
operator|.
name|Field
operator|.
name|Index
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
operator|.
name|Store
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
operator|.
name|TermVector
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
name|IndexWriterConfig
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
name|TermFreqVector
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
name|TermPositionVector
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
name|TermVectorOffsetInfo
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
name|queryParser
operator|.
name|QueryParser
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
name|store
operator|.
name|Directory
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
name|Version
import|;
end_import
begin_comment
comment|/**  *<code>FieldTermStack</code> is a stack that keeps query terms in the specified field  * of the document to be highlighted.  */
end_comment
begin_class
DECL|class|FieldTermStack
specifier|public
class|class
name|FieldTermStack
block|{
DECL|field|fieldName
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
DECL|field|termList
name|LinkedList
argument_list|<
name|TermInfo
argument_list|>
name|termList
init|=
operator|new
name|LinkedList
argument_list|<
name|TermInfo
argument_list|>
argument_list|()
decl_stmt|;
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
throws|throws
name|Exception
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|WhitespaceAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
decl_stmt|;
name|QueryParser
name|parser
init|=
operator|new
name|QueryParser
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
literal|"f"
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
name|parser
operator|.
name|parse
argument_list|(
literal|"a x:b"
argument_list|)
decl_stmt|;
name|FieldQuery
name|fieldQuery
init|=
operator|new
name|FieldQuery
argument_list|(
name|query
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
name|analyzer
argument_list|)
argument_list|)
decl_stmt|;
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
literal|"f"
argument_list|,
literal|"a a a b b c a b b c d e f"
argument_list|,
name|Store
operator|.
name|YES
argument_list|,
name|Index
operator|.
name|ANALYZED
argument_list|,
name|TermVector
operator|.
name|WITH_POSITIONS_OFFSETS
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"f"
argument_list|,
literal|"b a b a f"
argument_list|,
name|Store
operator|.
name|YES
argument_list|,
name|Index
operator|.
name|ANALYZED
argument_list|,
name|TermVector
operator|.
name|WITH_POSITIONS_OFFSETS
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
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
decl_stmt|;
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
literal|"f"
argument_list|,
name|fieldQuery
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * a constructor.    *     * @param reader IndexReader of the index    * @param docId document id to be highlighted    * @param fieldName field of the document to be highlighted    * @param fieldQuery FieldQuery object    * @throws IOException    */
DECL|method|FieldTermStack
specifier|public
name|FieldTermStack
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docId
parameter_list|,
name|String
name|fieldName
parameter_list|,
specifier|final
name|FieldQuery
name|fieldQuery
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|TermFreqVector
name|tfv
init|=
name|reader
operator|.
name|getTermFreqVector
argument_list|(
name|docId
argument_list|,
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|tfv
operator|==
literal|null
condition|)
return|return;
comment|// just return to make null snippets
name|TermPositionVector
name|tpv
init|=
literal|null
decl_stmt|;
try|try
block|{
name|tpv
operator|=
operator|(
name|TermPositionVector
operator|)
name|tfv
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassCastException
name|e
parameter_list|)
block|{
return|return;
comment|// just return to make null snippets
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|termSet
init|=
name|fieldQuery
operator|.
name|getTermSet
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
comment|// just return to make null snippet if un-matched fieldName specified when fieldMatch == true
if|if
condition|(
name|termSet
operator|==
literal|null
condition|)
return|return;
for|for
control|(
name|String
name|term
range|:
name|tpv
operator|.
name|getTerms
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|termSet
operator|.
name|contains
argument_list|(
name|term
argument_list|)
condition|)
continue|continue;
name|int
name|index
init|=
name|tpv
operator|.
name|indexOf
argument_list|(
name|term
argument_list|)
decl_stmt|;
name|TermVectorOffsetInfo
index|[]
name|tvois
init|=
name|tpv
operator|.
name|getOffsets
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|tvois
operator|==
literal|null
condition|)
return|return;
comment|// just return to make null snippets
name|int
index|[]
name|poss
init|=
name|tpv
operator|.
name|getTermPositions
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|poss
operator|==
literal|null
condition|)
return|return;
comment|// just return to make null snippets
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|tvois
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|termList
operator|.
name|add
argument_list|(
operator|new
name|TermInfo
argument_list|(
name|term
argument_list|,
name|tvois
index|[
name|i
index|]
operator|.
name|getStartOffset
argument_list|()
argument_list|,
name|tvois
index|[
name|i
index|]
operator|.
name|getEndOffset
argument_list|()
argument_list|,
name|poss
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// sort by position
name|Collections
operator|.
name|sort
argument_list|(
name|termList
argument_list|)
expr_stmt|;
block|}
comment|/**    * @return field name    */
DECL|method|getFieldName
specifier|public
name|String
name|getFieldName
parameter_list|()
block|{
return|return
name|fieldName
return|;
block|}
comment|/**    * @return the top TermInfo object of the stack    */
DECL|method|pop
specifier|public
name|TermInfo
name|pop
parameter_list|()
block|{
return|return
name|termList
operator|.
name|poll
argument_list|()
return|;
block|}
comment|/**    * @param termInfo the TermInfo object to be put on the top of the stack    */
DECL|method|push
specifier|public
name|void
name|push
parameter_list|(
name|TermInfo
name|termInfo
parameter_list|)
block|{
comment|// termList.push( termInfo );  // avoid Java 1.6 feature
name|termList
operator|.
name|addFirst
argument_list|(
name|termInfo
argument_list|)
expr_stmt|;
block|}
comment|/**    * to know whether the stack is empty    *     * @return true if the stack is empty, false if not    */
DECL|method|isEmpty
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|termList
operator|==
literal|null
operator|||
name|termList
operator|.
name|size
argument_list|()
operator|==
literal|0
return|;
block|}
DECL|class|TermInfo
specifier|public
specifier|static
class|class
name|TermInfo
implements|implements
name|Comparable
argument_list|<
name|TermInfo
argument_list|>
block|{
DECL|field|text
specifier|final
name|String
name|text
decl_stmt|;
DECL|field|startOffset
specifier|final
name|int
name|startOffset
decl_stmt|;
DECL|field|endOffset
specifier|final
name|int
name|endOffset
decl_stmt|;
DECL|field|position
specifier|final
name|int
name|position
decl_stmt|;
DECL|method|TermInfo
name|TermInfo
parameter_list|(
name|String
name|text
parameter_list|,
name|int
name|startOffset
parameter_list|,
name|int
name|endOffset
parameter_list|,
name|int
name|position
parameter_list|)
block|{
name|this
operator|.
name|text
operator|=
name|text
expr_stmt|;
name|this
operator|.
name|startOffset
operator|=
name|startOffset
expr_stmt|;
name|this
operator|.
name|endOffset
operator|=
name|endOffset
expr_stmt|;
name|this
operator|.
name|position
operator|=
name|position
expr_stmt|;
block|}
DECL|method|getText
specifier|public
name|String
name|getText
parameter_list|()
block|{
return|return
name|text
return|;
block|}
DECL|method|getStartOffset
specifier|public
name|int
name|getStartOffset
parameter_list|()
block|{
return|return
name|startOffset
return|;
block|}
DECL|method|getEndOffset
specifier|public
name|int
name|getEndOffset
parameter_list|()
block|{
return|return
name|endOffset
return|;
block|}
DECL|method|getPosition
specifier|public
name|int
name|getPosition
parameter_list|()
block|{
return|return
name|position
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
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
name|text
argument_list|)
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
operator|.
name|append
argument_list|(
name|startOffset
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|endOffset
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|position
argument_list|)
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|TermInfo
name|o
parameter_list|)
block|{
return|return
operator|(
name|this
operator|.
name|position
operator|-
name|o
operator|.
name|position
operator|)
return|;
block|}
block|}
block|}
end_class
end_unit
