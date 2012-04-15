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
name|Collections
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
name|search
operator|.
name|TopDocs
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
name|util
operator|.
name|_TestUtil
import|;
end_import
begin_class
DECL|class|TestRandomStoredFields
specifier|public
class|class
name|TestRandomStoredFields
extends|extends
name|LuceneTestCase
block|{
DECL|method|testRandomStoredFields
specifier|public
name|void
name|testRandomStoredFields
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
name|Random
name|rand
init|=
name|random
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|rand
argument_list|,
name|dir
argument_list|,
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
operator|.
name|setMaxBufferedDocs
argument_list|(
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|rand
argument_list|,
literal|5
argument_list|,
literal|20
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|//w.w.setUseCompoundFile(false);
specifier|final
name|int
name|docCount
init|=
name|atLeast
argument_list|(
literal|200
argument_list|)
decl_stmt|;
specifier|final
name|int
name|fieldCount
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|rand
argument_list|,
literal|1
argument_list|,
literal|5
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|fieldIDs
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
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
name|setTokenized
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Field
name|idField
init|=
name|newField
argument_list|(
literal|"id"
argument_list|,
literal|""
argument_list|,
name|customType
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
name|fieldCount
condition|;
name|i
operator|++
control|)
block|{
name|fieldIDs
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
specifier|final
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
argument_list|<
name|String
argument_list|,
name|Document
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: build index docCount="
operator|+
name|docCount
argument_list|)
expr_stmt|;
block|}
name|FieldType
name|customType2
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|customType2
operator|.
name|setStored
argument_list|(
literal|true
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
name|docCount
condition|;
name|i
operator|++
control|)
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
name|idField
argument_list|)
expr_stmt|;
specifier|final
name|String
name|id
init|=
literal|""
operator|+
name|i
decl_stmt|;
name|idField
operator|.
name|setStringValue
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|docs
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: add doc id="
operator|+
name|id
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|field
range|:
name|fieldIDs
control|)
block|{
specifier|final
name|String
name|s
decl_stmt|;
if|if
condition|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
operator|!=
literal|3
condition|)
block|{
name|s
operator|=
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|rand
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"f"
operator|+
name|field
argument_list|,
name|s
argument_list|,
name|customType2
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|s
operator|=
literal|null
expr_stmt|;
block|}
block|}
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|50
argument_list|)
operator|==
literal|17
condition|)
block|{
comment|// mixup binding of field name -> Number every so often
name|Collections
operator|.
name|shuffle
argument_list|(
name|fieldIDs
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
operator|==
literal|3
operator|&&
name|i
operator|>
literal|0
condition|)
block|{
specifier|final
name|String
name|delID
init|=
literal|""
operator|+
name|rand
operator|.
name|nextInt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: delete doc id="
operator|+
name|delID
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|delID
argument_list|)
argument_list|)
expr_stmt|;
name|docs
operator|.
name|remove
argument_list|(
name|delID
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: "
operator|+
name|docs
operator|.
name|size
argument_list|()
operator|+
literal|" docs in index; now load fields"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|docs
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|String
index|[]
name|idsList
init|=
name|docs
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|docs
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
literal|2
condition|;
name|x
operator|++
control|)
block|{
name|IndexReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|IndexSearcher
name|s
init|=
name|newSearcher
argument_list|(
name|r
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: cycle x="
operator|+
name|x
operator|+
literal|" r="
operator|+
name|r
argument_list|)
expr_stmt|;
block|}
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|num
condition|;
name|iter
operator|++
control|)
block|{
name|String
name|testID
init|=
name|idsList
index|[
name|rand
operator|.
name|nextInt
argument_list|(
name|idsList
operator|.
name|length
argument_list|)
index|]
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: test id="
operator|+
name|testID
argument_list|)
expr_stmt|;
block|}
name|TopDocs
name|hits
init|=
name|s
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|testID
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
name|r
operator|.
name|document
argument_list|(
name|hits
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|Document
name|docExp
init|=
name|docs
operator|.
name|get
argument_list|(
name|testID
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
name|fieldCount
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"doc "
operator|+
name|testID
operator|+
literal|", field f"
operator|+
name|fieldCount
operator|+
literal|" is wrong"
argument_list|,
name|docExp
operator|.
name|get
argument_list|(
literal|"f"
operator|+
name|i
argument_list|)
argument_list|,
name|doc
operator|.
name|get
argument_list|(
literal|"f"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
name|w
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
