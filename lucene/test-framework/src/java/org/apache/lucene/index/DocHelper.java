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
name|io
operator|.
name|UnsupportedEncodingException
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
name|analysis
operator|.
name|MockTokenizer
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
name|FieldType
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
name|StringField
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
name|FieldInfo
operator|.
name|IndexOptions
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
name|similarities
operator|.
name|Similarity
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
begin_class
DECL|class|DocHelper
class|class
name|DocHelper
block|{
DECL|field|customType
specifier|public
specifier|static
specifier|final
name|FieldType
name|customType
decl_stmt|;
DECL|field|FIELD_1_TEXT
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_1_TEXT
init|=
literal|"field one text"
decl_stmt|;
DECL|field|TEXT_FIELD_1_KEY
specifier|public
specifier|static
specifier|final
name|String
name|TEXT_FIELD_1_KEY
init|=
literal|"textField1"
decl_stmt|;
DECL|field|textField1
specifier|public
specifier|static
name|Field
name|textField1
decl_stmt|;
static|static
block|{
name|customType
operator|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
expr_stmt|;
name|textField1
operator|=
operator|new
name|Field
argument_list|(
name|TEXT_FIELD_1_KEY
argument_list|,
name|FIELD_1_TEXT
argument_list|,
name|customType
argument_list|)
expr_stmt|;
block|}
DECL|field|customType2
specifier|public
specifier|static
specifier|final
name|FieldType
name|customType2
decl_stmt|;
DECL|field|FIELD_2_TEXT
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_2_TEXT
init|=
literal|"field field field two text"
decl_stmt|;
comment|//Fields will be lexicographically sorted.  So, the order is: field, text, two
DECL|field|FIELD_2_FREQS
specifier|public
specifier|static
specifier|final
name|int
index|[]
name|FIELD_2_FREQS
init|=
block|{
literal|3
block|,
literal|1
block|,
literal|1
block|}
decl_stmt|;
DECL|field|TEXT_FIELD_2_KEY
specifier|public
specifier|static
specifier|final
name|String
name|TEXT_FIELD_2_KEY
init|=
literal|"textField2"
decl_stmt|;
DECL|field|textField2
specifier|public
specifier|static
name|Field
name|textField2
decl_stmt|;
static|static
block|{
name|customType2
operator|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
expr_stmt|;
name|customType2
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType2
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType2
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|textField2
operator|=
operator|new
name|Field
argument_list|(
name|TEXT_FIELD_2_KEY
argument_list|,
name|FIELD_2_TEXT
argument_list|,
name|customType2
argument_list|)
expr_stmt|;
block|}
DECL|field|customType3
specifier|public
specifier|static
specifier|final
name|FieldType
name|customType3
decl_stmt|;
DECL|field|FIELD_3_TEXT
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_3_TEXT
init|=
literal|"aaaNoNorms aaaNoNorms bbbNoNorms"
decl_stmt|;
DECL|field|TEXT_FIELD_3_KEY
specifier|public
specifier|static
specifier|final
name|String
name|TEXT_FIELD_3_KEY
init|=
literal|"textField3"
decl_stmt|;
DECL|field|textField3
specifier|public
specifier|static
name|Field
name|textField3
decl_stmt|;
static|static
block|{
name|customType3
operator|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
expr_stmt|;
name|customType3
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|textField3
operator|=
operator|new
name|Field
argument_list|(
name|TEXT_FIELD_3_KEY
argument_list|,
name|FIELD_3_TEXT
argument_list|,
name|customType3
argument_list|)
expr_stmt|;
block|}
DECL|field|KEYWORD_TEXT
specifier|public
specifier|static
specifier|final
name|String
name|KEYWORD_TEXT
init|=
literal|"Keyword"
decl_stmt|;
DECL|field|KEYWORD_FIELD_KEY
specifier|public
specifier|static
specifier|final
name|String
name|KEYWORD_FIELD_KEY
init|=
literal|"keyField"
decl_stmt|;
DECL|field|keyField
specifier|public
specifier|static
name|Field
name|keyField
decl_stmt|;
static|static
block|{
name|keyField
operator|=
operator|new
name|StringField
argument_list|(
name|KEYWORD_FIELD_KEY
argument_list|,
name|KEYWORD_TEXT
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
expr_stmt|;
block|}
DECL|field|customType5
specifier|public
specifier|static
specifier|final
name|FieldType
name|customType5
decl_stmt|;
DECL|field|NO_NORMS_TEXT
specifier|public
specifier|static
specifier|final
name|String
name|NO_NORMS_TEXT
init|=
literal|"omitNormsText"
decl_stmt|;
DECL|field|NO_NORMS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|NO_NORMS_KEY
init|=
literal|"omitNorms"
decl_stmt|;
DECL|field|noNormsField
specifier|public
specifier|static
name|Field
name|noNormsField
decl_stmt|;
static|static
block|{
name|customType5
operator|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
expr_stmt|;
name|customType5
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType5
operator|.
name|setTokenized
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|noNormsField
operator|=
operator|new
name|Field
argument_list|(
name|NO_NORMS_KEY
argument_list|,
name|NO_NORMS_TEXT
argument_list|,
name|customType5
argument_list|)
expr_stmt|;
block|}
DECL|field|customType6
specifier|public
specifier|static
specifier|final
name|FieldType
name|customType6
decl_stmt|;
DECL|field|NO_TF_TEXT
specifier|public
specifier|static
specifier|final
name|String
name|NO_TF_TEXT
init|=
literal|"analyzed with no tf and positions"
decl_stmt|;
DECL|field|NO_TF_KEY
specifier|public
specifier|static
specifier|final
name|String
name|NO_TF_KEY
init|=
literal|"omitTermFreqAndPositions"
decl_stmt|;
DECL|field|noTFField
specifier|public
specifier|static
name|Field
name|noTFField
decl_stmt|;
static|static
block|{
name|customType6
operator|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
expr_stmt|;
name|customType6
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_ONLY
argument_list|)
expr_stmt|;
name|noTFField
operator|=
operator|new
name|Field
argument_list|(
name|NO_TF_KEY
argument_list|,
name|NO_TF_TEXT
argument_list|,
name|customType6
argument_list|)
expr_stmt|;
block|}
DECL|field|customType7
specifier|public
specifier|static
specifier|final
name|FieldType
name|customType7
decl_stmt|;
DECL|field|UNINDEXED_FIELD_TEXT
specifier|public
specifier|static
specifier|final
name|String
name|UNINDEXED_FIELD_TEXT
init|=
literal|"unindexed field text"
decl_stmt|;
DECL|field|UNINDEXED_FIELD_KEY
specifier|public
specifier|static
specifier|final
name|String
name|UNINDEXED_FIELD_KEY
init|=
literal|"unIndField"
decl_stmt|;
DECL|field|unIndField
specifier|public
specifier|static
name|Field
name|unIndField
decl_stmt|;
static|static
block|{
name|customType7
operator|=
operator|new
name|FieldType
argument_list|()
expr_stmt|;
name|customType7
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|unIndField
operator|=
operator|new
name|Field
argument_list|(
name|UNINDEXED_FIELD_KEY
argument_list|,
name|UNINDEXED_FIELD_TEXT
argument_list|,
name|customType7
argument_list|)
expr_stmt|;
block|}
DECL|field|UNSTORED_1_FIELD_TEXT
specifier|public
specifier|static
specifier|final
name|String
name|UNSTORED_1_FIELD_TEXT
init|=
literal|"unstored field text"
decl_stmt|;
DECL|field|UNSTORED_FIELD_1_KEY
specifier|public
specifier|static
specifier|final
name|String
name|UNSTORED_FIELD_1_KEY
init|=
literal|"unStoredField1"
decl_stmt|;
DECL|field|unStoredField1
specifier|public
specifier|static
name|Field
name|unStoredField1
init|=
operator|new
name|TextField
argument_list|(
name|UNSTORED_FIELD_1_KEY
argument_list|,
name|UNSTORED_1_FIELD_TEXT
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
decl_stmt|;
DECL|field|customType8
specifier|public
specifier|static
specifier|final
name|FieldType
name|customType8
decl_stmt|;
DECL|field|UNSTORED_2_FIELD_TEXT
specifier|public
specifier|static
specifier|final
name|String
name|UNSTORED_2_FIELD_TEXT
init|=
literal|"unstored field text"
decl_stmt|;
DECL|field|UNSTORED_FIELD_2_KEY
specifier|public
specifier|static
specifier|final
name|String
name|UNSTORED_FIELD_2_KEY
init|=
literal|"unStoredField2"
decl_stmt|;
DECL|field|unStoredField2
specifier|public
specifier|static
name|Field
name|unStoredField2
decl_stmt|;
static|static
block|{
name|customType8
operator|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
expr_stmt|;
name|customType8
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|unStoredField2
operator|=
operator|new
name|Field
argument_list|(
name|UNSTORED_FIELD_2_KEY
argument_list|,
name|UNSTORED_2_FIELD_TEXT
argument_list|,
name|customType8
argument_list|)
expr_stmt|;
block|}
DECL|field|LAZY_FIELD_BINARY_KEY
specifier|public
specifier|static
specifier|final
name|String
name|LAZY_FIELD_BINARY_KEY
init|=
literal|"lazyFieldBinary"
decl_stmt|;
DECL|field|LAZY_FIELD_BINARY_BYTES
specifier|public
specifier|static
name|byte
index|[]
name|LAZY_FIELD_BINARY_BYTES
decl_stmt|;
DECL|field|lazyFieldBinary
specifier|public
specifier|static
name|Field
name|lazyFieldBinary
decl_stmt|;
DECL|field|LAZY_FIELD_KEY
specifier|public
specifier|static
specifier|final
name|String
name|LAZY_FIELD_KEY
init|=
literal|"lazyField"
decl_stmt|;
DECL|field|LAZY_FIELD_TEXT
specifier|public
specifier|static
specifier|final
name|String
name|LAZY_FIELD_TEXT
init|=
literal|"These are some field bytes"
decl_stmt|;
DECL|field|lazyField
specifier|public
specifier|static
name|Field
name|lazyField
init|=
operator|new
name|Field
argument_list|(
name|LAZY_FIELD_KEY
argument_list|,
name|LAZY_FIELD_TEXT
argument_list|,
name|customType
argument_list|)
decl_stmt|;
DECL|field|LARGE_LAZY_FIELD_KEY
specifier|public
specifier|static
specifier|final
name|String
name|LARGE_LAZY_FIELD_KEY
init|=
literal|"largeLazyField"
decl_stmt|;
DECL|field|LARGE_LAZY_FIELD_TEXT
specifier|public
specifier|static
name|String
name|LARGE_LAZY_FIELD_TEXT
decl_stmt|;
DECL|field|largeLazyField
specifier|public
specifier|static
name|Field
name|largeLazyField
decl_stmt|;
comment|//From Issue 509
DECL|field|FIELD_UTF1_TEXT
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_UTF1_TEXT
init|=
literal|"field one \u4e00text"
decl_stmt|;
DECL|field|TEXT_FIELD_UTF1_KEY
specifier|public
specifier|static
specifier|final
name|String
name|TEXT_FIELD_UTF1_KEY
init|=
literal|"textField1Utf8"
decl_stmt|;
DECL|field|textUtfField1
specifier|public
specifier|static
name|Field
name|textUtfField1
init|=
operator|new
name|Field
argument_list|(
name|TEXT_FIELD_UTF1_KEY
argument_list|,
name|FIELD_UTF1_TEXT
argument_list|,
name|customType
argument_list|)
decl_stmt|;
DECL|field|FIELD_UTF2_TEXT
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_UTF2_TEXT
init|=
literal|"field field field \u4e00two text"
decl_stmt|;
comment|//Fields will be lexicographically sorted.  So, the order is: field, text, two
DECL|field|FIELD_UTF2_FREQS
specifier|public
specifier|static
specifier|final
name|int
index|[]
name|FIELD_UTF2_FREQS
init|=
block|{
literal|3
block|,
literal|1
block|,
literal|1
block|}
decl_stmt|;
DECL|field|TEXT_FIELD_UTF2_KEY
specifier|public
specifier|static
specifier|final
name|String
name|TEXT_FIELD_UTF2_KEY
init|=
literal|"textField2Utf8"
decl_stmt|;
DECL|field|textUtfField2
specifier|public
specifier|static
name|Field
name|textUtfField2
init|=
operator|new
name|Field
argument_list|(
name|TEXT_FIELD_UTF2_KEY
argument_list|,
name|FIELD_UTF2_TEXT
argument_list|,
name|customType2
argument_list|)
decl_stmt|;
DECL|field|nameValues
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|nameValues
init|=
literal|null
decl_stmt|;
comment|// ordered list of all the fields...
comment|// could use LinkedHashMap for this purpose if Java1.4 is OK
DECL|field|fields
specifier|public
specifier|static
name|Field
index|[]
name|fields
init|=
operator|new
name|Field
index|[]
block|{
name|textField1
block|,
name|textField2
block|,
name|textField3
block|,
name|keyField
block|,
name|noNormsField
block|,
name|noTFField
block|,
name|unIndField
block|,
name|unStoredField1
block|,
name|unStoredField2
block|,
name|textUtfField1
block|,
name|textUtfField2
block|,
name|lazyField
block|,
name|lazyFieldBinary
block|,
comment|//placeholder for binary field, since this is null.  It must be second to last.
name|largeLazyField
comment|//placeholder for large field, since this is null.  It must always be last
block|}
decl_stmt|;
DECL|field|all
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|IndexableField
argument_list|>
name|all
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|indexed
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|IndexableField
argument_list|>
name|indexed
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|stored
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|IndexableField
argument_list|>
name|stored
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|unstored
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|IndexableField
argument_list|>
name|unstored
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|unindexed
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|IndexableField
argument_list|>
name|unindexed
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|termvector
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|IndexableField
argument_list|>
name|termvector
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|notermvector
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|IndexableField
argument_list|>
name|notermvector
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|lazy
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|IndexableField
argument_list|>
name|lazy
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|noNorms
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|IndexableField
argument_list|>
name|noNorms
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|noTf
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|IndexableField
argument_list|>
name|noTf
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
static|static
block|{
comment|//Initialize the large Lazy Field
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
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
literal|10000
condition|;
name|i
operator|++
control|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"Lazily loading lengths of language in lieu of laughing "
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|LAZY_FIELD_BINARY_BYTES
operator|=
literal|"These are some binary field bytes"
operator|.
name|getBytes
argument_list|(
literal|"UTF8"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{     }
name|lazyFieldBinary
operator|=
operator|new
name|StoredField
argument_list|(
name|LAZY_FIELD_BINARY_KEY
argument_list|,
name|LAZY_FIELD_BINARY_BYTES
argument_list|)
expr_stmt|;
name|fields
index|[
name|fields
operator|.
name|length
operator|-
literal|2
index|]
operator|=
name|lazyFieldBinary
expr_stmt|;
name|LARGE_LAZY_FIELD_TEXT
operator|=
name|buffer
operator|.
name|toString
argument_list|()
expr_stmt|;
name|largeLazyField
operator|=
operator|new
name|Field
argument_list|(
name|LARGE_LAZY_FIELD_KEY
argument_list|,
name|LARGE_LAZY_FIELD_TEXT
argument_list|,
name|customType
argument_list|)
expr_stmt|;
name|fields
index|[
name|fields
operator|.
name|length
operator|-
literal|1
index|]
operator|=
name|largeLazyField
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
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|IndexableField
name|f
init|=
name|fields
index|[
name|i
index|]
decl_stmt|;
name|add
argument_list|(
name|all
argument_list|,
name|f
argument_list|)
expr_stmt|;
if|if
condition|(
name|f
operator|.
name|fieldType
argument_list|()
operator|.
name|indexed
argument_list|()
condition|)
name|add
argument_list|(
name|indexed
argument_list|,
name|f
argument_list|)
expr_stmt|;
else|else
name|add
argument_list|(
name|unindexed
argument_list|,
name|f
argument_list|)
expr_stmt|;
if|if
condition|(
name|f
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectors
argument_list|()
condition|)
name|add
argument_list|(
name|termvector
argument_list|,
name|f
argument_list|)
expr_stmt|;
if|if
condition|(
name|f
operator|.
name|fieldType
argument_list|()
operator|.
name|indexed
argument_list|()
operator|&&
operator|!
name|f
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectors
argument_list|()
condition|)
name|add
argument_list|(
name|notermvector
argument_list|,
name|f
argument_list|)
expr_stmt|;
if|if
condition|(
name|f
operator|.
name|fieldType
argument_list|()
operator|.
name|stored
argument_list|()
condition|)
name|add
argument_list|(
name|stored
argument_list|,
name|f
argument_list|)
expr_stmt|;
else|else
name|add
argument_list|(
name|unstored
argument_list|,
name|f
argument_list|)
expr_stmt|;
if|if
condition|(
name|f
operator|.
name|fieldType
argument_list|()
operator|.
name|indexOptions
argument_list|()
operator|==
name|IndexOptions
operator|.
name|DOCS_ONLY
condition|)
name|add
argument_list|(
name|noTf
argument_list|,
name|f
argument_list|)
expr_stmt|;
if|if
condition|(
name|f
operator|.
name|fieldType
argument_list|()
operator|.
name|omitNorms
argument_list|()
condition|)
name|add
argument_list|(
name|noNorms
argument_list|,
name|f
argument_list|)
expr_stmt|;
if|if
condition|(
name|f
operator|.
name|fieldType
argument_list|()
operator|.
name|indexOptions
argument_list|()
operator|==
name|IndexOptions
operator|.
name|DOCS_ONLY
condition|)
name|add
argument_list|(
name|noTf
argument_list|,
name|f
argument_list|)
expr_stmt|;
comment|//if (f.isLazy()) add(lazy, f);
block|}
block|}
DECL|method|add
specifier|private
specifier|static
name|void
name|add
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|IndexableField
argument_list|>
name|map
parameter_list|,
name|IndexableField
name|field
parameter_list|)
block|{
name|map
operator|.
name|put
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
static|static
block|{
name|nameValues
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|nameValues
operator|.
name|put
argument_list|(
name|TEXT_FIELD_1_KEY
argument_list|,
name|FIELD_1_TEXT
argument_list|)
expr_stmt|;
name|nameValues
operator|.
name|put
argument_list|(
name|TEXT_FIELD_2_KEY
argument_list|,
name|FIELD_2_TEXT
argument_list|)
expr_stmt|;
name|nameValues
operator|.
name|put
argument_list|(
name|TEXT_FIELD_3_KEY
argument_list|,
name|FIELD_3_TEXT
argument_list|)
expr_stmt|;
name|nameValues
operator|.
name|put
argument_list|(
name|KEYWORD_FIELD_KEY
argument_list|,
name|KEYWORD_TEXT
argument_list|)
expr_stmt|;
name|nameValues
operator|.
name|put
argument_list|(
name|NO_NORMS_KEY
argument_list|,
name|NO_NORMS_TEXT
argument_list|)
expr_stmt|;
name|nameValues
operator|.
name|put
argument_list|(
name|NO_TF_KEY
argument_list|,
name|NO_TF_TEXT
argument_list|)
expr_stmt|;
name|nameValues
operator|.
name|put
argument_list|(
name|UNINDEXED_FIELD_KEY
argument_list|,
name|UNINDEXED_FIELD_TEXT
argument_list|)
expr_stmt|;
name|nameValues
operator|.
name|put
argument_list|(
name|UNSTORED_FIELD_1_KEY
argument_list|,
name|UNSTORED_1_FIELD_TEXT
argument_list|)
expr_stmt|;
name|nameValues
operator|.
name|put
argument_list|(
name|UNSTORED_FIELD_2_KEY
argument_list|,
name|UNSTORED_2_FIELD_TEXT
argument_list|)
expr_stmt|;
name|nameValues
operator|.
name|put
argument_list|(
name|LAZY_FIELD_KEY
argument_list|,
name|LAZY_FIELD_TEXT
argument_list|)
expr_stmt|;
name|nameValues
operator|.
name|put
argument_list|(
name|LAZY_FIELD_BINARY_KEY
argument_list|,
name|LAZY_FIELD_BINARY_BYTES
argument_list|)
expr_stmt|;
name|nameValues
operator|.
name|put
argument_list|(
name|LARGE_LAZY_FIELD_KEY
argument_list|,
name|LARGE_LAZY_FIELD_TEXT
argument_list|)
expr_stmt|;
name|nameValues
operator|.
name|put
argument_list|(
name|TEXT_FIELD_UTF1_KEY
argument_list|,
name|FIELD_UTF1_TEXT
argument_list|)
expr_stmt|;
name|nameValues
operator|.
name|put
argument_list|(
name|TEXT_FIELD_UTF2_KEY
argument_list|,
name|FIELD_UTF2_TEXT
argument_list|)
expr_stmt|;
block|}
comment|/**    * Adds the fields above to a document     * @param doc The document to write    */
DECL|method|setupDoc
specifier|public
specifier|static
name|void
name|setupDoc
parameter_list|(
name|Document
name|doc
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|fields
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Writes the document to the directory using a segment    * named "test"; returns the SegmentInfo describing the new    * segment     */
DECL|method|writeDoc
specifier|public
specifier|static
name|SegmentCommitInfo
name|writeDoc
parameter_list|(
name|Random
name|random
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|Document
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|writeDoc
argument_list|(
name|random
argument_list|,
name|dir
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|,
literal|null
argument_list|,
name|doc
argument_list|)
return|;
block|}
comment|/**    * Writes the document to the directory using the analyzer    * and the similarity score; returns the SegmentInfo    * describing the new segment    */
DECL|method|writeDoc
specifier|public
specifier|static
name|SegmentCommitInfo
name|writeDoc
parameter_list|(
name|Random
name|random
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|Similarity
name|similarity
parameter_list|,
name|Document
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
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
comment|/* LuceneTestCase.newIndexWriterConfig(random, */
name|analyzer
argument_list|)
operator|.
name|setSimilarity
argument_list|(
name|similarity
operator|==
literal|null
condition|?
name|IndexSearcher
operator|.
name|getDefaultSimilarity
argument_list|()
else|:
name|similarity
argument_list|)
argument_list|)
decl_stmt|;
comment|//writer.setNoCFSRatio(0.0);
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|SegmentCommitInfo
name|info
init|=
name|writer
operator|.
name|newestSegment
argument_list|()
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|info
return|;
block|}
DECL|method|numFields
specifier|public
specifier|static
name|int
name|numFields
parameter_list|(
name|Document
name|doc
parameter_list|)
block|{
return|return
name|doc
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|numFields
specifier|public
specifier|static
name|int
name|numFields
parameter_list|(
name|StoredDocument
name|doc
parameter_list|)
block|{
return|return
name|doc
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|createDocument
specifier|public
specifier|static
name|Document
name|createDocument
parameter_list|(
name|int
name|n
parameter_list|,
name|String
name|indexName
parameter_list|,
name|int
name|numFields
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|FieldType
name|customType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|customType
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|FieldType
name|customType1
init|=
operator|new
name|FieldType
argument_list|(
name|StringField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|customType1
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType1
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType1
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
specifier|final
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
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|n
argument_list|)
argument_list|,
name|customType1
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
literal|"indexname"
argument_list|,
name|indexName
argument_list|,
name|customType1
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"field1"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
name|customType
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" b"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|n
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|numFields
condition|;
name|i
operator|++
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"field"
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
name|customType
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|doc
return|;
block|}
block|}
end_class
end_unit
