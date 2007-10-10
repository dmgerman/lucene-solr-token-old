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
name|SimpleAnalyzer
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
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Array
import|;
end_import
begin_comment
comment|/**  * Test demonstrating EOF bug on the last field of the last doc   * if other docs have allready been accessed.  */
end_comment
begin_class
DECL|class|TestLazyBug
specifier|public
class|class
name|TestLazyBug
extends|extends
name|LuceneTestCase
block|{
DECL|field|BASE_SEED
specifier|public
specifier|static
name|int
name|BASE_SEED
init|=
literal|13
decl_stmt|;
DECL|field|NUM_DOCS
specifier|public
specifier|static
name|int
name|NUM_DOCS
init|=
literal|500
decl_stmt|;
DECL|field|NUM_FIELDS
specifier|public
specifier|static
name|int
name|NUM_FIELDS
init|=
literal|100
decl_stmt|;
DECL|field|data
specifier|private
specifier|static
name|String
index|[]
name|data
init|=
operator|new
name|String
index|[]
block|{
literal|"now"
block|,
literal|"is the time"
block|,
literal|"for all good men"
block|,
literal|"to come to the aid"
block|,
literal|"of their country!"
block|,
literal|"this string contains big chars:{\u0111 \u0222 \u0333 \u1111 \u2222 \u3333}"
block|,
literal|"this string is a bigger string, mary had a little lamb, little lamb, little lamb!"
block|}
decl_stmt|;
DECL|field|dataset
specifier|private
specifier|static
name|Set
name|dataset
init|=
operator|new
name|HashSet
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|data
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|MAGIC_FIELD
specifier|private
specifier|static
name|String
name|MAGIC_FIELD
init|=
literal|"f"
operator|+
operator|(
name|NUM_FIELDS
operator|/
literal|3
operator|)
decl_stmt|;
DECL|field|SELECTOR
specifier|private
specifier|static
name|FieldSelector
name|SELECTOR
init|=
operator|new
name|FieldSelector
argument_list|()
block|{
specifier|public
name|FieldSelectorResult
name|accept
parameter_list|(
name|String
name|f
parameter_list|)
block|{
if|if
condition|(
name|f
operator|.
name|equals
argument_list|(
name|MAGIC_FIELD
argument_list|)
condition|)
block|{
return|return
name|FieldSelectorResult
operator|.
name|LOAD
return|;
block|}
return|return
name|FieldSelectorResult
operator|.
name|LAZY_LOAD
return|;
block|}
block|}
decl_stmt|;
DECL|method|makeIndex
specifier|private
specifier|static
name|Directory
name|makeIndex
parameter_list|()
throws|throws
name|RuntimeException
block|{
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
try|try
block|{
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
name|BASE_SEED
operator|+
literal|42
argument_list|)
decl_stmt|;
name|Analyzer
name|analyzer
init|=
operator|new
name|SimpleAnalyzer
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
name|analyzer
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
for|for
control|(
name|int
name|d
init|=
literal|1
init|;
name|d
operator|<=
name|NUM_DOCS
condition|;
name|d
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
for|for
control|(
name|int
name|f
init|=
literal|1
init|;
name|f
operator|<=
name|NUM_FIELDS
condition|;
name|f
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
literal|"f"
operator|+
name|f
argument_list|,
name|data
index|[
name|f
operator|%
name|data
operator|.
name|length
index|]
operator|+
literal|'#'
operator|+
name|data
index|[
name|r
operator|.
name|nextInt
argument_list|(
name|data
operator|.
name|length
argument_list|)
index|]
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
name|TOKENIZED
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
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|dir
return|;
block|}
DECL|method|doTest
specifier|public
specifier|static
name|void
name|doTest
parameter_list|(
name|int
index|[]
name|docs
parameter_list|)
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|makeIndex
argument_list|()
decl_stmt|;
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
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
name|docs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|d
init|=
name|reader
operator|.
name|document
argument_list|(
name|docs
index|[
name|i
index|]
argument_list|,
name|SELECTOR
argument_list|)
decl_stmt|;
name|String
name|trash
init|=
name|d
operator|.
name|get
argument_list|(
name|MAGIC_FIELD
argument_list|)
decl_stmt|;
name|List
name|fields
init|=
name|d
operator|.
name|getFields
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|fi
init|=
name|fields
operator|.
name|iterator
argument_list|()
init|;
name|fi
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Fieldable
name|f
init|=
literal|null
decl_stmt|;
try|try
block|{
name|f
operator|=
operator|(
name|Fieldable
operator|)
name|fi
operator|.
name|next
argument_list|()
expr_stmt|;
name|String
name|fname
init|=
name|f
operator|.
name|name
argument_list|()
decl_stmt|;
name|String
name|fval
init|=
name|f
operator|.
name|stringValue
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|docs
index|[
name|i
index|]
operator|+
literal|" FIELD: "
operator|+
name|fname
argument_list|,
name|fval
argument_list|)
expr_stmt|;
name|String
index|[]
name|vals
init|=
name|fval
operator|.
name|split
argument_list|(
literal|"#"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|dataset
operator|.
name|contains
argument_list|(
name|vals
index|[
literal|0
index|]
argument_list|)
operator|||
operator|!
name|dataset
operator|.
name|contains
argument_list|(
name|vals
index|[
literal|1
index|]
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"FIELD:"
operator|+
name|fname
operator|+
literal|",VAL:"
operator|+
name|fval
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
name|docs
index|[
name|i
index|]
operator|+
literal|" WTF: "
operator|+
name|f
operator|.
name|name
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testLazyWorks
specifier|public
name|void
name|testLazyWorks
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
operator|new
name|int
index|[]
block|{
literal|399
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testLazyAlsoWorks
specifier|public
name|void
name|testLazyAlsoWorks
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
operator|new
name|int
index|[]
block|{
literal|399
block|,
literal|150
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testLazyBroken
specifier|public
name|void
name|testLazyBroken
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
operator|new
name|int
index|[]
block|{
literal|150
block|,
literal|399
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
