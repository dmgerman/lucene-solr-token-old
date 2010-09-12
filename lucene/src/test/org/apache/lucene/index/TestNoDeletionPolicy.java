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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
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
name|Constructor
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
name|Method
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
name|Modifier
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
name|LuceneTestCaseJ4
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
DECL|class|TestNoDeletionPolicy
specifier|public
class|class
name|TestNoDeletionPolicy
extends|extends
name|LuceneTestCaseJ4
block|{
annotation|@
name|Test
DECL|method|testNoDeletionPolicy
specifier|public
name|void
name|testNoDeletionPolicy
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexDeletionPolicy
name|idp
init|=
name|NoDeletionPolicy
operator|.
name|INSTANCE
decl_stmt|;
name|idp
operator|.
name|onInit
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|idp
operator|.
name|onCommit
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFinalSingleton
specifier|public
name|void
name|testFinalSingleton
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|Modifier
operator|.
name|isFinal
argument_list|(
name|NoDeletionPolicy
operator|.
name|class
operator|.
name|getModifiers
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Constructor
argument_list|<
name|?
argument_list|>
index|[]
name|ctors
init|=
name|NoDeletionPolicy
operator|.
name|class
operator|.
name|getDeclaredConstructors
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"expected 1 private ctor only: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|ctors
argument_list|)
argument_list|,
literal|1
argument_list|,
name|ctors
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"that 1 should be private: "
operator|+
name|ctors
index|[
literal|0
index|]
argument_list|,
name|Modifier
operator|.
name|isPrivate
argument_list|(
name|ctors
index|[
literal|0
index|]
operator|.
name|getModifiers
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMethodsOverridden
specifier|public
name|void
name|testMethodsOverridden
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Ensures that all methods of IndexDeletionPolicy are
comment|// overridden/implemented. That's important to ensure that NoDeletionPolicy
comment|// overrides everything, so that no unexpected behavior/error occurs.
comment|// NOTE: even though IndexDeletionPolicy is an interface today, and so all
comment|// methods must be implemented by NoDeletionPolicy, this test is important
comment|// in case one day IDP becomes an abstract class.
for|for
control|(
name|Method
name|m
range|:
name|NoDeletionPolicy
operator|.
name|class
operator|.
name|getMethods
argument_list|()
control|)
block|{
comment|// getDeclaredMethods() returns just those methods that are declared on
comment|// NoDeletionPolicy. getMethods() returns those that are visible in that
comment|// context, including ones from Object. So just filter out Object. If in
comment|// the future IndexDeletionPolicy will become a class that extends a
comment|// different class than Object, this will need to change.
if|if
condition|(
name|m
operator|.
name|getDeclaringClass
argument_list|()
operator|!=
name|Object
operator|.
name|class
condition|)
block|{
name|assertTrue
argument_list|(
name|m
operator|+
literal|" is not overridden !"
argument_list|,
name|m
operator|.
name|getDeclaringClass
argument_list|()
operator|==
name|NoDeletionPolicy
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testAllCommitsRemain
specifier|public
name|void
name|testAllCommitsRemain
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
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
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
operator|.
name|setIndexDeletionPolicy
argument_list|(
name|NoDeletionPolicy
operator|.
name|INSTANCE
argument_list|)
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
literal|10
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
name|newField
argument_list|(
literal|"c"
argument_list|,
literal|"a"
operator|+
name|i
argument_list|,
name|Store
operator|.
name|YES
argument_list|,
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
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong number of commits !"
argument_list|,
name|i
operator|+
literal|1
argument_list|,
name|IndexReader
operator|.
name|listCommits
argument_list|(
name|dir
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|writer
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
