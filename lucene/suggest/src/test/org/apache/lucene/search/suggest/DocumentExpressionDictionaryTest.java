begin_unit
begin_package
DECL|package|org.apache.lucene.search.suggest
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
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
name|ArrayList
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
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|Random
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
name|MockAnalyzer
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
name|NumericDocValuesField
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
name|StoredField
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
name|TextField
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
name|DirectoryReader
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
name|RandomIndexWriter
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
name|Term
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
name|SortField
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
name|spell
operator|.
name|Dictionary
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
name|util
operator|.
name|BytesRef
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
name|junit
operator|.
name|Test
import|;
end_import
begin_class
DECL|class|DocumentExpressionDictionaryTest
specifier|public
class|class
name|DocumentExpressionDictionaryTest
extends|extends
name|LuceneTestCase
block|{
DECL|field|FIELD_NAME
specifier|static
specifier|final
name|String
name|FIELD_NAME
init|=
literal|"f1"
decl_stmt|;
DECL|field|WEIGHT_FIELD_NAME_1
specifier|static
specifier|final
name|String
name|WEIGHT_FIELD_NAME_1
init|=
literal|"w1"
decl_stmt|;
DECL|field|WEIGHT_FIELD_NAME_2
specifier|static
specifier|final
name|String
name|WEIGHT_FIELD_NAME_2
init|=
literal|"w2"
decl_stmt|;
DECL|field|WEIGHT_FIELD_NAME_3
specifier|static
specifier|final
name|String
name|WEIGHT_FIELD_NAME_3
init|=
literal|"w3"
decl_stmt|;
DECL|field|PAYLOAD_FIELD_NAME
specifier|static
specifier|final
name|String
name|PAYLOAD_FIELD_NAME
init|=
literal|"p1"
decl_stmt|;
DECL|method|generateIndexDocuments
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Document
argument_list|>
name|generateIndexDocuments
parameter_list|(
name|int
name|ndocs
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Document
argument_list|>
name|docs
init|=
operator|new
name|HashMap
argument_list|<>
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
name|ndocs
condition|;
name|i
operator|++
control|)
block|{
name|Field
name|field
init|=
operator|new
name|TextField
argument_list|(
name|FIELD_NAME
argument_list|,
literal|"field_"
operator|+
name|i
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
decl_stmt|;
name|Field
name|payload
init|=
operator|new
name|StoredField
argument_list|(
name|PAYLOAD_FIELD_NAME
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"payload_"
operator|+
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|Field
name|weight1
init|=
operator|new
name|NumericDocValuesField
argument_list|(
name|WEIGHT_FIELD_NAME_1
argument_list|,
literal|10
operator|+
name|i
argument_list|)
decl_stmt|;
name|Field
name|weight2
init|=
operator|new
name|NumericDocValuesField
argument_list|(
name|WEIGHT_FIELD_NAME_2
argument_list|,
literal|20
operator|+
name|i
argument_list|)
decl_stmt|;
name|Field
name|weight3
init|=
operator|new
name|NumericDocValuesField
argument_list|(
name|WEIGHT_FIELD_NAME_3
argument_list|,
literal|30
operator|+
name|i
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
name|field
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|payload
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|weight1
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|weight2
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|weight3
argument_list|)
expr_stmt|;
name|docs
operator|.
name|put
argument_list|(
name|field
operator|.
name|stringValue
argument_list|()
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
return|return
name|docs
return|;
block|}
annotation|@
name|Test
DECL|method|testBasic
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Document
argument_list|>
name|docs
init|=
name|generateIndexDocuments
argument_list|(
literal|10
argument_list|)
decl_stmt|;
for|for
control|(
name|Document
name|doc
range|:
name|docs
operator|.
name|values
argument_list|()
control|)
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|ir
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|SortField
argument_list|>
name|sortFields
init|=
operator|new
name|HashSet
argument_list|<
name|SortField
argument_list|>
argument_list|()
decl_stmt|;
name|sortFields
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
name|WEIGHT_FIELD_NAME_1
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|)
argument_list|)
expr_stmt|;
name|sortFields
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
name|WEIGHT_FIELD_NAME_2
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|)
argument_list|)
expr_stmt|;
name|sortFields
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
name|WEIGHT_FIELD_NAME_3
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|)
argument_list|)
expr_stmt|;
name|Dictionary
name|dictionary
init|=
operator|new
name|DocumentExpressionDictionary
argument_list|(
name|ir
argument_list|,
name|FIELD_NAME
argument_list|,
literal|"((w1 + w2) - w3)"
argument_list|,
name|sortFields
argument_list|,
name|PAYLOAD_FIELD_NAME
argument_list|)
decl_stmt|;
name|InputIterator
name|tfp
init|=
operator|(
name|InputIterator
operator|)
name|dictionary
operator|.
name|getWordsIterator
argument_list|()
decl_stmt|;
name|BytesRef
name|f
decl_stmt|;
while|while
condition|(
operator|(
name|f
operator|=
name|tfp
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|Document
name|doc
init|=
name|docs
operator|.
name|remove
argument_list|(
name|f
operator|.
name|utf8ToString
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|w1
init|=
name|doc
operator|.
name|getField
argument_list|(
name|WEIGHT_FIELD_NAME_1
argument_list|)
operator|.
name|numericValue
argument_list|()
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|long
name|w2
init|=
name|doc
operator|.
name|getField
argument_list|(
name|WEIGHT_FIELD_NAME_2
argument_list|)
operator|.
name|numericValue
argument_list|()
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|long
name|w3
init|=
name|doc
operator|.
name|getField
argument_list|(
name|WEIGHT_FIELD_NAME_3
argument_list|)
operator|.
name|numericValue
argument_list|()
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|equals
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|doc
operator|.
name|get
argument_list|(
name|FIELD_NAME
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|tfp
operator|.
name|weight
argument_list|()
argument_list|,
operator|(
name|w1
operator|+
name|w2
operator|)
operator|-
name|w3
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tfp
operator|.
name|payload
argument_list|()
operator|.
name|equals
argument_list|(
name|doc
operator|.
name|getField
argument_list|(
name|PAYLOAD_FIELD_NAME
argument_list|)
operator|.
name|binaryValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|docs
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWithoutPayload
specifier|public
name|void
name|testWithoutPayload
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Document
argument_list|>
name|docs
init|=
name|generateIndexDocuments
argument_list|(
literal|10
argument_list|)
decl_stmt|;
for|for
control|(
name|Document
name|doc
range|:
name|docs
operator|.
name|values
argument_list|()
control|)
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|ir
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|SortField
argument_list|>
name|sortFields
init|=
operator|new
name|HashSet
argument_list|<
name|SortField
argument_list|>
argument_list|()
decl_stmt|;
name|sortFields
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
name|WEIGHT_FIELD_NAME_1
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|)
argument_list|)
expr_stmt|;
name|sortFields
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
name|WEIGHT_FIELD_NAME_2
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|)
argument_list|)
expr_stmt|;
name|sortFields
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
name|WEIGHT_FIELD_NAME_3
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|)
argument_list|)
expr_stmt|;
name|Dictionary
name|dictionary
init|=
operator|new
name|DocumentExpressionDictionary
argument_list|(
name|ir
argument_list|,
name|FIELD_NAME
argument_list|,
literal|"w1 + (0.2 * w2) - (w3 - w1)/2"
argument_list|,
name|sortFields
argument_list|)
decl_stmt|;
name|InputIterator
name|tfp
init|=
operator|(
name|InputIterator
operator|)
name|dictionary
operator|.
name|getWordsIterator
argument_list|()
decl_stmt|;
name|BytesRef
name|f
decl_stmt|;
while|while
condition|(
operator|(
name|f
operator|=
name|tfp
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|Document
name|doc
init|=
name|docs
operator|.
name|remove
argument_list|(
name|f
operator|.
name|utf8ToString
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|w1
init|=
name|doc
operator|.
name|getField
argument_list|(
name|WEIGHT_FIELD_NAME_1
argument_list|)
operator|.
name|numericValue
argument_list|()
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|long
name|w2
init|=
name|doc
operator|.
name|getField
argument_list|(
name|WEIGHT_FIELD_NAME_2
argument_list|)
operator|.
name|numericValue
argument_list|()
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|long
name|w3
init|=
name|doc
operator|.
name|getField
argument_list|(
name|WEIGHT_FIELD_NAME_3
argument_list|)
operator|.
name|numericValue
argument_list|()
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|equals
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|doc
operator|.
name|get
argument_list|(
name|FIELD_NAME
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|tfp
operator|.
name|weight
argument_list|()
argument_list|,
call|(
name|long
call|)
argument_list|(
name|w1
operator|+
operator|(
literal|0.2
operator|*
name|w2
operator|)
operator|-
operator|(
name|w3
operator|-
name|w1
operator|)
operator|/
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|tfp
operator|.
name|payload
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|docs
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWithDeletions
specifier|public
name|void
name|testWithDeletions
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Document
argument_list|>
name|docs
init|=
name|generateIndexDocuments
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|Random
name|rand
init|=
name|random
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|termsToDel
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Document
name|doc
range|:
name|docs
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|rand
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|termsToDel
operator|.
name|add
argument_list|(
name|doc
operator|.
name|get
argument_list|(
name|FIELD_NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Term
index|[]
name|delTerms
init|=
operator|new
name|Term
index|[
name|termsToDel
operator|.
name|size
argument_list|()
index|]
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
name|termsToDel
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|delTerms
index|[
name|i
index|]
operator|=
operator|new
name|Term
argument_list|(
name|FIELD_NAME
argument_list|,
name|termsToDel
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Term
name|delTerm
range|:
name|delTerms
control|)
block|{
name|writer
operator|.
name|deleteDocuments
argument_list|(
name|delTerm
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|termToDel
range|:
name|termsToDel
control|)
block|{
name|assertTrue
argument_list|(
literal|null
operator|!=
name|docs
operator|.
name|remove
argument_list|(
name|termToDel
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|IndexReader
name|ir
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ir
operator|.
name|numDocs
argument_list|()
argument_list|,
name|docs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|SortField
argument_list|>
name|sortFields
init|=
operator|new
name|HashSet
argument_list|<
name|SortField
argument_list|>
argument_list|()
decl_stmt|;
name|sortFields
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
name|WEIGHT_FIELD_NAME_1
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|)
argument_list|)
expr_stmt|;
name|sortFields
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
name|WEIGHT_FIELD_NAME_2
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|)
argument_list|)
expr_stmt|;
name|Dictionary
name|dictionary
init|=
operator|new
name|DocumentExpressionDictionary
argument_list|(
name|ir
argument_list|,
name|FIELD_NAME
argument_list|,
literal|"w2-w1"
argument_list|,
name|sortFields
argument_list|,
name|PAYLOAD_FIELD_NAME
argument_list|)
decl_stmt|;
name|InputIterator
name|tfp
init|=
operator|(
name|InputIterator
operator|)
name|dictionary
operator|.
name|getWordsIterator
argument_list|()
decl_stmt|;
name|BytesRef
name|f
decl_stmt|;
while|while
condition|(
operator|(
name|f
operator|=
name|tfp
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|Document
name|doc
init|=
name|docs
operator|.
name|remove
argument_list|(
name|f
operator|.
name|utf8ToString
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|w1
init|=
name|doc
operator|.
name|getField
argument_list|(
name|WEIGHT_FIELD_NAME_1
argument_list|)
operator|.
name|numericValue
argument_list|()
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|long
name|w2
init|=
name|doc
operator|.
name|getField
argument_list|(
name|WEIGHT_FIELD_NAME_2
argument_list|)
operator|.
name|numericValue
argument_list|()
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|equals
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|doc
operator|.
name|get
argument_list|(
name|FIELD_NAME
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|tfp
operator|.
name|weight
argument_list|()
argument_list|,
name|w2
operator|-
name|w1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tfp
operator|.
name|payload
argument_list|()
operator|.
name|equals
argument_list|(
name|doc
operator|.
name|getField
argument_list|(
name|PAYLOAD_FIELD_NAME
argument_list|)
operator|.
name|binaryValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|docs
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit