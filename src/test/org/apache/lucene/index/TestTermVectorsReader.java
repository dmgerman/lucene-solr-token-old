begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|lucene
operator|.
name|store
operator|.
name|MockRAMDirectory
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
name|TokenStream
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
name|Token
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
name|Document
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
name|io
operator|.
name|Reader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedSet
import|;
end_import
begin_class
DECL|class|TestTermVectorsReader
specifier|public
class|class
name|TestTermVectorsReader
extends|extends
name|LuceneTestCase
block|{
comment|//Must be lexicographically sorted, will do in setup, versus trying to maintain here
DECL|field|testFields
specifier|private
name|String
index|[]
name|testFields
init|=
block|{
literal|"f1"
block|,
literal|"f2"
block|,
literal|"f3"
block|,
literal|"f4"
block|}
decl_stmt|;
DECL|field|testFieldsStorePos
specifier|private
name|boolean
index|[]
name|testFieldsStorePos
init|=
block|{
literal|true
block|,
literal|false
block|,
literal|true
block|,
literal|false
block|}
decl_stmt|;
DECL|field|testFieldsStoreOff
specifier|private
name|boolean
index|[]
name|testFieldsStoreOff
init|=
block|{
literal|true
block|,
literal|false
block|,
literal|false
block|,
literal|true
block|}
decl_stmt|;
DECL|field|testTerms
specifier|private
name|String
index|[]
name|testTerms
init|=
block|{
literal|"this"
block|,
literal|"is"
block|,
literal|"a"
block|,
literal|"test"
block|}
decl_stmt|;
DECL|field|positions
specifier|private
name|int
index|[]
index|[]
name|positions
init|=
operator|new
name|int
index|[
name|testTerms
operator|.
name|length
index|]
index|[]
decl_stmt|;
DECL|field|offsets
specifier|private
name|TermVectorOffsetInfo
index|[]
index|[]
name|offsets
init|=
operator|new
name|TermVectorOffsetInfo
index|[
name|testTerms
operator|.
name|length
index|]
index|[]
decl_stmt|;
DECL|field|dir
specifier|private
name|MockRAMDirectory
name|dir
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
DECL|field|seg
specifier|private
name|String
name|seg
decl_stmt|;
DECL|field|fieldInfos
specifier|private
name|FieldInfos
name|fieldInfos
init|=
operator|new
name|FieldInfos
argument_list|()
decl_stmt|;
DECL|field|TERM_FREQ
specifier|private
specifier|static
name|int
name|TERM_FREQ
init|=
literal|3
decl_stmt|;
DECL|method|TestTermVectorsReader
specifier|public
name|TestTermVectorsReader
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|super
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
DECL|class|TestToken
specifier|private
class|class
name|TestToken
implements|implements
name|Comparable
block|{
DECL|field|text
name|String
name|text
decl_stmt|;
DECL|field|pos
name|int
name|pos
decl_stmt|;
DECL|field|startOffset
name|int
name|startOffset
decl_stmt|;
DECL|field|endOffset
name|int
name|endOffset
decl_stmt|;
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
return|return
name|pos
operator|-
operator|(
operator|(
name|TestToken
operator|)
name|other
operator|)
operator|.
name|pos
return|;
block|}
block|}
DECL|field|tokens
name|TestToken
index|[]
name|tokens
init|=
operator|new
name|TestToken
index|[
name|testTerms
operator|.
name|length
operator|*
name|TERM_FREQ
index|]
decl_stmt|;
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
comment|/*     for (int i = 0; i< testFields.length; i++) {       fieldInfos.add(testFields[i], true, true, testFieldsStorePos[i], testFieldsStoreOff[i]);     }     */
name|Arrays
operator|.
name|sort
argument_list|(
name|testTerms
argument_list|)
expr_stmt|;
name|int
name|tokenUpto
init|=
literal|0
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
name|testTerms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|positions
index|[
name|i
index|]
operator|=
operator|new
name|int
index|[
name|TERM_FREQ
index|]
expr_stmt|;
name|offsets
index|[
name|i
index|]
operator|=
operator|new
name|TermVectorOffsetInfo
index|[
name|TERM_FREQ
index|]
expr_stmt|;
comment|// first position must be 0
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|TERM_FREQ
condition|;
name|j
operator|++
control|)
block|{
comment|// positions are always sorted in increasing order
name|positions
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
call|(
name|int
call|)
argument_list|(
name|j
operator|*
literal|10
operator|+
name|Math
operator|.
name|random
argument_list|()
operator|*
literal|10
argument_list|)
expr_stmt|;
comment|// offsets are always sorted in increasing order
name|offsets
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
operator|new
name|TermVectorOffsetInfo
argument_list|(
name|j
operator|*
literal|10
argument_list|,
name|j
operator|*
literal|10
operator|+
name|testTerms
index|[
name|i
index|]
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|TestToken
name|token
init|=
name|tokens
index|[
name|tokenUpto
operator|++
index|]
operator|=
operator|new
name|TestToken
argument_list|()
decl_stmt|;
name|token
operator|.
name|text
operator|=
name|testTerms
index|[
name|i
index|]
expr_stmt|;
name|token
operator|.
name|pos
operator|=
name|positions
index|[
name|i
index|]
index|[
name|j
index|]
expr_stmt|;
name|token
operator|.
name|startOffset
operator|=
name|offsets
index|[
name|i
index|]
index|[
name|j
index|]
operator|.
name|getStartOffset
argument_list|()
expr_stmt|;
name|token
operator|.
name|endOffset
operator|=
name|offsets
index|[
name|i
index|]
index|[
name|j
index|]
operator|.
name|getEndOffset
argument_list|()
expr_stmt|;
block|}
block|}
name|Arrays
operator|.
name|sort
argument_list|(
name|tokens
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|MyAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|writer
operator|.
name|setUseCompoundFile
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
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
name|testFields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Field
operator|.
name|TermVector
name|tv
decl_stmt|;
if|if
condition|(
name|testFieldsStorePos
index|[
name|i
index|]
operator|&&
name|testFieldsStoreOff
index|[
name|i
index|]
condition|)
name|tv
operator|=
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS_OFFSETS
expr_stmt|;
elseif|else
if|if
condition|(
name|testFieldsStorePos
index|[
name|i
index|]
operator|&&
operator|!
name|testFieldsStoreOff
index|[
name|i
index|]
condition|)
name|tv
operator|=
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS
expr_stmt|;
elseif|else
if|if
condition|(
operator|!
name|testFieldsStorePos
index|[
name|i
index|]
operator|&&
name|testFieldsStoreOff
index|[
name|i
index|]
condition|)
name|tv
operator|=
name|Field
operator|.
name|TermVector
operator|.
name|WITH_OFFSETS
expr_stmt|;
else|else
name|tv
operator|=
name|Field
operator|.
name|TermVector
operator|.
name|YES
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|testFields
index|[
name|i
index|]
argument_list|,
literal|""
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|,
name|tv
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//Create 5 documents for testing, they all have the same
comment|//terms
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|5
condition|;
name|j
operator|++
control|)
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|seg
operator|=
name|writer
operator|.
name|newestSegment
argument_list|()
operator|.
name|name
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|fieldInfos
operator|=
operator|new
name|FieldInfos
argument_list|(
name|dir
argument_list|,
name|seg
operator|+
literal|"."
operator|+
name|IndexFileNames
operator|.
name|FIELD_INFOS_EXTENSION
argument_list|)
expr_stmt|;
block|}
DECL|class|MyTokenStream
specifier|private
class|class
name|MyTokenStream
extends|extends
name|TokenStream
block|{
DECL|field|tokenUpto
name|int
name|tokenUpto
decl_stmt|;
DECL|method|next
specifier|public
name|Token
name|next
parameter_list|()
block|{
if|if
condition|(
name|tokenUpto
operator|>=
name|tokens
operator|.
name|length
condition|)
return|return
literal|null
return|;
else|else
block|{
specifier|final
name|Token
name|t
init|=
operator|new
name|Token
argument_list|()
decl_stmt|;
specifier|final
name|TestToken
name|testToken
init|=
name|tokens
index|[
name|tokenUpto
operator|++
index|]
decl_stmt|;
name|t
operator|.
name|setTermText
argument_list|(
name|testToken
operator|.
name|text
argument_list|)
expr_stmt|;
if|if
condition|(
name|tokenUpto
operator|>
literal|1
condition|)
name|t
operator|.
name|setPositionIncrement
argument_list|(
name|testToken
operator|.
name|pos
operator|-
name|tokens
index|[
name|tokenUpto
operator|-
literal|2
index|]
operator|.
name|pos
argument_list|)
expr_stmt|;
else|else
name|t
operator|.
name|setPositionIncrement
argument_list|(
name|testToken
operator|.
name|pos
operator|+
literal|1
argument_list|)
expr_stmt|;
name|t
operator|.
name|setStartOffset
argument_list|(
name|testToken
operator|.
name|startOffset
argument_list|)
expr_stmt|;
name|t
operator|.
name|setEndOffset
argument_list|(
name|testToken
operator|.
name|endOffset
argument_list|)
expr_stmt|;
return|return
name|t
return|;
block|}
block|}
block|}
DECL|class|MyAnalyzer
specifier|private
class|class
name|MyAnalyzer
extends|extends
name|Analyzer
block|{
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|MyTokenStream
argument_list|()
return|;
block|}
block|}
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
block|{
comment|//Check to see the files were created properly in setup
name|assertTrue
argument_list|(
name|dir
operator|.
name|fileExists
argument_list|(
name|seg
operator|+
literal|"."
operator|+
name|IndexFileNames
operator|.
name|VECTORS_DOCUMENTS_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dir
operator|.
name|fileExists
argument_list|(
name|seg
operator|+
literal|"."
operator|+
name|IndexFileNames
operator|.
name|VECTORS_INDEX_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testReader
specifier|public
name|void
name|testReader
parameter_list|()
throws|throws
name|IOException
block|{
name|TermVectorsReader
name|reader
init|=
operator|new
name|TermVectorsReader
argument_list|(
name|dir
argument_list|,
name|seg
argument_list|,
name|fieldInfos
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|!=
literal|null
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|5
condition|;
name|j
operator|++
control|)
block|{
name|TermFreqVector
name|vector
init|=
name|reader
operator|.
name|get
argument_list|(
name|j
argument_list|,
name|testFields
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|vector
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|String
index|[]
name|terms
init|=
name|vector
operator|.
name|getTerms
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|terms
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|terms
operator|.
name|length
operator|==
name|testTerms
operator|.
name|length
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
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|term
init|=
name|terms
index|[
name|i
index|]
decl_stmt|;
comment|//System.out.println("Term: " + term);
name|assertTrue
argument_list|(
name|term
operator|.
name|equals
argument_list|(
name|testTerms
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testPositionReader
specifier|public
name|void
name|testPositionReader
parameter_list|()
throws|throws
name|IOException
block|{
name|TermVectorsReader
name|reader
init|=
operator|new
name|TermVectorsReader
argument_list|(
name|dir
argument_list|,
name|seg
argument_list|,
name|fieldInfos
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|TermPositionVector
name|vector
decl_stmt|;
name|String
index|[]
name|terms
decl_stmt|;
name|vector
operator|=
operator|(
name|TermPositionVector
operator|)
name|reader
operator|.
name|get
argument_list|(
literal|0
argument_list|,
name|testFields
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|vector
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|terms
operator|=
name|vector
operator|.
name|getTerms
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|terms
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|terms
operator|.
name|length
operator|==
name|testTerms
operator|.
name|length
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
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|term
init|=
name|terms
index|[
name|i
index|]
decl_stmt|;
comment|//System.out.println("Term: " + term);
name|assertTrue
argument_list|(
name|term
operator|.
name|equals
argument_list|(
name|testTerms
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|int
index|[]
name|positions
init|=
name|vector
operator|.
name|getTermPositions
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|positions
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|positions
operator|.
name|length
operator|==
name|this
operator|.
name|positions
index|[
name|i
index|]
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|positions
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|int
name|position
init|=
name|positions
index|[
name|j
index|]
decl_stmt|;
name|assertTrue
argument_list|(
name|position
operator|==
name|this
operator|.
name|positions
index|[
name|i
index|]
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
name|TermVectorOffsetInfo
index|[]
name|offset
init|=
name|vector
operator|.
name|getOffsets
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|offset
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|offset
operator|.
name|length
operator|==
name|this
operator|.
name|offsets
index|[
name|i
index|]
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|offset
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|TermVectorOffsetInfo
name|termVectorOffsetInfo
init|=
name|offset
index|[
name|j
index|]
decl_stmt|;
name|assertTrue
argument_list|(
name|termVectorOffsetInfo
operator|.
name|equals
argument_list|(
name|offsets
index|[
name|i
index|]
index|[
name|j
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|TermFreqVector
name|freqVector
init|=
name|reader
operator|.
name|get
argument_list|(
literal|0
argument_list|,
name|testFields
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
comment|//no pos, no offset
name|assertTrue
argument_list|(
name|freqVector
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|freqVector
operator|instanceof
name|TermPositionVector
operator|==
literal|false
argument_list|)
expr_stmt|;
name|terms
operator|=
name|freqVector
operator|.
name|getTerms
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|terms
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|terms
operator|.
name|length
operator|==
name|testTerms
operator|.
name|length
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
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|term
init|=
name|terms
index|[
name|i
index|]
decl_stmt|;
comment|//System.out.println("Term: " + term);
name|assertTrue
argument_list|(
name|term
operator|.
name|equals
argument_list|(
name|testTerms
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testOffsetReader
specifier|public
name|void
name|testOffsetReader
parameter_list|()
throws|throws
name|IOException
block|{
name|TermVectorsReader
name|reader
init|=
operator|new
name|TermVectorsReader
argument_list|(
name|dir
argument_list|,
name|seg
argument_list|,
name|fieldInfos
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|TermPositionVector
name|vector
init|=
operator|(
name|TermPositionVector
operator|)
name|reader
operator|.
name|get
argument_list|(
literal|0
argument_list|,
name|testFields
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|vector
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|String
index|[]
name|terms
init|=
name|vector
operator|.
name|getTerms
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|terms
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|terms
operator|.
name|length
operator|==
name|testTerms
operator|.
name|length
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
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|term
init|=
name|terms
index|[
name|i
index|]
decl_stmt|;
comment|//System.out.println("Term: " + term);
name|assertTrue
argument_list|(
name|term
operator|.
name|equals
argument_list|(
name|testTerms
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|int
index|[]
name|positions
init|=
name|vector
operator|.
name|getTermPositions
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|positions
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|positions
operator|.
name|length
operator|==
name|this
operator|.
name|positions
index|[
name|i
index|]
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|positions
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|int
name|position
init|=
name|positions
index|[
name|j
index|]
decl_stmt|;
name|assertTrue
argument_list|(
name|position
operator|==
name|this
operator|.
name|positions
index|[
name|i
index|]
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
name|TermVectorOffsetInfo
index|[]
name|offset
init|=
name|vector
operator|.
name|getOffsets
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|offset
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|offset
operator|.
name|length
operator|==
name|this
operator|.
name|offsets
index|[
name|i
index|]
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|offset
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|TermVectorOffsetInfo
name|termVectorOffsetInfo
init|=
name|offset
index|[
name|j
index|]
decl_stmt|;
name|assertTrue
argument_list|(
name|termVectorOffsetInfo
operator|.
name|equals
argument_list|(
name|offsets
index|[
name|i
index|]
index|[
name|j
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testMapper
specifier|public
name|void
name|testMapper
parameter_list|()
throws|throws
name|IOException
block|{
name|TermVectorsReader
name|reader
init|=
operator|new
name|TermVectorsReader
argument_list|(
name|dir
argument_list|,
name|seg
argument_list|,
name|fieldInfos
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|SortedTermVectorMapper
name|mapper
init|=
operator|new
name|SortedTermVectorMapper
argument_list|(
operator|new
name|TermVectorEntryFreqSortedComparator
argument_list|()
argument_list|)
decl_stmt|;
name|reader
operator|.
name|get
argument_list|(
literal|0
argument_list|,
name|mapper
argument_list|)
expr_stmt|;
name|SortedSet
name|set
init|=
name|mapper
operator|.
name|getTermVectorEntrySet
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"set is null and it shouldn't be"
argument_list|,
name|set
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|//three fields, 4 terms, all terms are the same
name|assertTrue
argument_list|(
literal|"set Size: "
operator|+
name|set
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|4
argument_list|,
name|set
operator|.
name|size
argument_list|()
operator|==
literal|4
argument_list|)
expr_stmt|;
comment|//Check offsets and positions
for|for
control|(
name|Iterator
name|iterator
init|=
name|set
operator|.
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|TermVectorEntry
name|tve
init|=
operator|(
name|TermVectorEntry
operator|)
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"tve is null and it shouldn't be"
argument_list|,
name|tve
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"tve.getOffsets() is null and it shouldn't be"
argument_list|,
name|tve
operator|.
name|getOffsets
argument_list|()
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"tve.getPositions() is null and it shouldn't be"
argument_list|,
name|tve
operator|.
name|getPositions
argument_list|()
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
name|mapper
operator|=
operator|new
name|SortedTermVectorMapper
argument_list|(
operator|new
name|TermVectorEntryFreqSortedComparator
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|get
argument_list|(
literal|1
argument_list|,
name|mapper
argument_list|)
expr_stmt|;
name|set
operator|=
name|mapper
operator|.
name|getTermVectorEntrySet
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"set is null and it shouldn't be"
argument_list|,
name|set
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|//three fields, 4 terms, all terms are the same
name|assertTrue
argument_list|(
literal|"set Size: "
operator|+
name|set
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|4
argument_list|,
name|set
operator|.
name|size
argument_list|()
operator|==
literal|4
argument_list|)
expr_stmt|;
comment|//Should have offsets and positions b/c we are munging all the fields together
for|for
control|(
name|Iterator
name|iterator
init|=
name|set
operator|.
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|TermVectorEntry
name|tve
init|=
operator|(
name|TermVectorEntry
operator|)
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"tve is null and it shouldn't be"
argument_list|,
name|tve
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"tve.getOffsets() is null and it shouldn't be"
argument_list|,
name|tve
operator|.
name|getOffsets
argument_list|()
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"tve.getPositions() is null and it shouldn't be"
argument_list|,
name|tve
operator|.
name|getPositions
argument_list|()
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
name|FieldSortedTermVectorMapper
name|fsMapper
init|=
operator|new
name|FieldSortedTermVectorMapper
argument_list|(
operator|new
name|TermVectorEntryFreqSortedComparator
argument_list|()
argument_list|)
decl_stmt|;
name|reader
operator|.
name|get
argument_list|(
literal|0
argument_list|,
name|fsMapper
argument_list|)
expr_stmt|;
name|Map
name|map
init|=
name|fsMapper
operator|.
name|getFieldToTerms
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"map Size: "
operator|+
name|map
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
name|testFields
operator|.
name|length
argument_list|,
name|map
operator|.
name|size
argument_list|()
operator|==
name|testFields
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|iterator
init|=
name|map
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|SortedSet
name|sortedSet
init|=
operator|(
name|SortedSet
operator|)
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"sortedSet Size: "
operator|+
name|sortedSet
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|4
argument_list|,
name|sortedSet
operator|.
name|size
argument_list|()
operator|==
literal|4
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|inner
init|=
name|sortedSet
operator|.
name|iterator
argument_list|()
init|;
name|inner
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|TermVectorEntry
name|tve
init|=
operator|(
name|TermVectorEntry
operator|)
name|inner
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"tve is null and it shouldn't be"
argument_list|,
name|tve
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|//Check offsets and positions.
name|assertTrue
argument_list|(
literal|"tve is null and it shouldn't be"
argument_list|,
name|tve
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|String
name|field
init|=
name|tve
operator|.
name|getField
argument_list|()
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|equals
argument_list|(
name|testFields
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
comment|//should have offsets
name|assertTrue
argument_list|(
literal|"tve.getOffsets() is null and it shouldn't be"
argument_list|,
name|tve
operator|.
name|getOffsets
argument_list|()
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"tve.getPositions() is null and it shouldn't be"
argument_list|,
name|tve
operator|.
name|getPositions
argument_list|()
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|field
operator|.
name|equals
argument_list|(
name|testFields
index|[
literal|1
index|]
argument_list|)
condition|)
block|{
comment|//should not have offsets
name|assertTrue
argument_list|(
literal|"tve.getOffsets() is not null and it shouldn't be"
argument_list|,
name|tve
operator|.
name|getOffsets
argument_list|()
operator|==
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"tve.getPositions() is not null and it shouldn't be"
argument_list|,
name|tve
operator|.
name|getPositions
argument_list|()
operator|==
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|//Try mapper that ignores offs and positions
name|fsMapper
operator|=
operator|new
name|FieldSortedTermVectorMapper
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|,
operator|new
name|TermVectorEntryFreqSortedComparator
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|get
argument_list|(
literal|0
argument_list|,
name|fsMapper
argument_list|)
expr_stmt|;
name|map
operator|=
name|fsMapper
operator|.
name|getFieldToTerms
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"map Size: "
operator|+
name|map
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
name|testFields
operator|.
name|length
argument_list|,
name|map
operator|.
name|size
argument_list|()
operator|==
name|testFields
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|iterator
init|=
name|map
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|SortedSet
name|sortedSet
init|=
operator|(
name|SortedSet
operator|)
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"sortedSet Size: "
operator|+
name|sortedSet
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|4
argument_list|,
name|sortedSet
operator|.
name|size
argument_list|()
operator|==
literal|4
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|inner
init|=
name|sortedSet
operator|.
name|iterator
argument_list|()
init|;
name|inner
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|TermVectorEntry
name|tve
init|=
operator|(
name|TermVectorEntry
operator|)
name|inner
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"tve is null and it shouldn't be"
argument_list|,
name|tve
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|//Check offsets and positions.
name|assertTrue
argument_list|(
literal|"tve is null and it shouldn't be"
argument_list|,
name|tve
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|String
name|field
init|=
name|tve
operator|.
name|getField
argument_list|()
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|equals
argument_list|(
name|testFields
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
comment|//should have offsets
name|assertTrue
argument_list|(
literal|"tve.getOffsets() is null and it shouldn't be"
argument_list|,
name|tve
operator|.
name|getOffsets
argument_list|()
operator|==
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"tve.getPositions() is null and it shouldn't be"
argument_list|,
name|tve
operator|.
name|getPositions
argument_list|()
operator|==
literal|null
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|field
operator|.
name|equals
argument_list|(
name|testFields
index|[
literal|1
index|]
argument_list|)
condition|)
block|{
comment|//should not have offsets
name|assertTrue
argument_list|(
literal|"tve.getOffsets() is not null and it shouldn't be"
argument_list|,
name|tve
operator|.
name|getOffsets
argument_list|()
operator|==
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"tve.getPositions() is not null and it shouldn't be"
argument_list|,
name|tve
operator|.
name|getPositions
argument_list|()
operator|==
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * Make sure exceptions and bad params are handled appropriately    */
DECL|method|testBadParams
specifier|public
name|void
name|testBadParams
parameter_list|()
block|{
try|try
block|{
name|TermVectorsReader
name|reader
init|=
operator|new
name|TermVectorsReader
argument_list|(
name|dir
argument_list|,
name|seg
argument_list|,
name|fieldInfos
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|//Bad document number, good field number
name|reader
operator|.
name|get
argument_list|(
literal|50
argument_list|,
name|testFields
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// expected exception
block|}
try|try
block|{
name|TermVectorsReader
name|reader
init|=
operator|new
name|TermVectorsReader
argument_list|(
name|dir
argument_list|,
name|seg
argument_list|,
name|fieldInfos
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|//Bad document number, no field
name|reader
operator|.
name|get
argument_list|(
literal|50
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// expected exception
block|}
try|try
block|{
name|TermVectorsReader
name|reader
init|=
operator|new
name|TermVectorsReader
argument_list|(
name|dir
argument_list|,
name|seg
argument_list|,
name|fieldInfos
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|//good document number, bad field number
name|TermFreqVector
name|vector
init|=
name|reader
operator|.
name|get
argument_list|(
literal|0
argument_list|,
literal|"f50"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|vector
operator|==
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
