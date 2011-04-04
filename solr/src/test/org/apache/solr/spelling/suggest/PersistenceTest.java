begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.spelling.suggest
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
operator|.
name|suggest
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|SolrTestCaseJ4
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
operator|.
name|suggest
operator|.
name|jaspell
operator|.
name|JaspellLookup
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
operator|.
name|suggest
operator|.
name|tst
operator|.
name|TSTLookup
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
DECL|class|PersistenceTest
specifier|public
class|class
name|PersistenceTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|keys
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|keys
init|=
operator|new
name|String
index|[]
block|{
literal|"one"
block|,
literal|"two"
block|,
literal|"three"
block|,
literal|"four"
block|,
literal|"oneness"
block|,
literal|"onerous"
block|,
literal|"onesimus"
block|,
literal|"twofold"
block|,
literal|"twonk"
block|,
literal|"thrive"
block|,
literal|"through"
block|,
literal|"threat"
block|,
literal|"foundation"
block|,
literal|"fourier"
block|,
literal|"fourty"
block|}
decl_stmt|;
annotation|@
name|Test
DECL|method|testTSTPersistence
specifier|public
name|void
name|testTSTPersistence
parameter_list|()
throws|throws
name|Exception
block|{
name|TSTLookup
name|lookup
init|=
operator|new
name|TSTLookup
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|k
range|:
name|keys
control|)
block|{
name|lookup
operator|.
name|add
argument_list|(
name|k
argument_list|,
operator|new
name|Float
argument_list|(
name|k
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|File
name|storeDir
init|=
operator|new
name|File
argument_list|(
name|TEST_HOME
argument_list|()
argument_list|)
decl_stmt|;
name|lookup
operator|.
name|store
argument_list|(
name|storeDir
argument_list|)
expr_stmt|;
name|lookup
operator|=
operator|new
name|TSTLookup
argument_list|()
expr_stmt|;
name|lookup
operator|.
name|load
argument_list|(
name|storeDir
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|k
range|:
name|keys
control|)
block|{
name|Float
name|val
init|=
operator|(
name|Float
operator|)
name|lookup
operator|.
name|get
argument_list|(
name|k
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|k
argument_list|,
name|val
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|k
argument_list|,
name|k
operator|.
name|length
argument_list|()
argument_list|,
name|val
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testJaspellPersistence
specifier|public
name|void
name|testJaspellPersistence
parameter_list|()
throws|throws
name|Exception
block|{
name|JaspellLookup
name|lookup
init|=
operator|new
name|JaspellLookup
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|k
range|:
name|keys
control|)
block|{
name|lookup
operator|.
name|add
argument_list|(
name|k
argument_list|,
operator|new
name|Float
argument_list|(
name|k
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|File
name|storeDir
init|=
operator|new
name|File
argument_list|(
name|TEST_HOME
argument_list|()
argument_list|)
decl_stmt|;
name|lookup
operator|.
name|store
argument_list|(
name|storeDir
argument_list|)
expr_stmt|;
name|lookup
operator|=
operator|new
name|JaspellLookup
argument_list|()
expr_stmt|;
name|lookup
operator|.
name|load
argument_list|(
name|storeDir
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|k
range|:
name|keys
control|)
block|{
name|Float
name|val
init|=
operator|(
name|Float
operator|)
name|lookup
operator|.
name|get
argument_list|(
name|k
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|k
argument_list|,
name|val
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|k
argument_list|,
name|k
operator|.
name|length
argument_list|()
argument_list|,
name|val
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
