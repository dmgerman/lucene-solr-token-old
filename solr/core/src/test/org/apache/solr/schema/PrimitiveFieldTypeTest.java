begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
package|;
end_package
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
name|core
operator|.
name|SolrConfig
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
name|core
operator|.
name|SolrResourceLoader
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
begin_comment
comment|/**  * Tests that defaults are set for Primitive (non-analyzed) fields  */
end_comment
begin_class
DECL|class|PrimitiveFieldTypeTest
specifier|public
class|class
name|PrimitiveFieldTypeTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|testConfHome
specifier|private
specifier|final
name|String
name|testConfHome
init|=
name|TEST_HOME
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"collection1"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"conf"
operator|+
name|File
operator|.
name|separator
decl_stmt|;
DECL|field|config
specifier|protected
name|SolrConfig
name|config
decl_stmt|;
DECL|field|schema
specifier|protected
name|IndexSchema
name|schema
decl_stmt|;
DECL|field|initMap
specifier|protected
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|initMap
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
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
comment|// set some system properties for use by tests
name|System
operator|.
name|setProperty
argument_list|(
literal|"enable.update.log"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
comment|// schema12 doesn't support _version_
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.test.sys.prop1"
argument_list|,
literal|"propone"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.test.sys.prop2"
argument_list|,
literal|"proptwo"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.allow.unsafe.resourceloading"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|initMap
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|config
operator|=
operator|new
name|SolrConfig
argument_list|(
operator|new
name|SolrResourceLoader
argument_list|(
literal|"solr/collection1"
argument_list|)
argument_list|,
name|testConfHome
operator|+
literal|"solrconfig.xml"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.allow.unsafe.resourceloading"
argument_list|)
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Test
DECL|method|testDefaultOmitNorms
specifier|public
name|void
name|testDefaultOmitNorms
parameter_list|()
throws|throws
name|Exception
block|{
name|BinaryField
name|bin
decl_stmt|;
name|TextField
name|t
decl_stmt|;
name|TrieDateField
name|dt
decl_stmt|;
name|StrField
name|s
decl_stmt|;
name|TrieIntField
name|ti
decl_stmt|;
name|TrieLongField
name|tl
decl_stmt|;
name|TrieFloatField
name|tf
decl_stmt|;
name|TrieDoubleField
name|td
decl_stmt|;
name|BoolField
name|b
decl_stmt|;
comment|// ***********************
comment|// With schema version 1.4:
comment|// ***********************
name|schema
operator|=
name|IndexSchemaFactory
operator|.
name|buildIndexSchema
argument_list|(
name|testConfHome
operator|+
literal|"schema12.xml"
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|dt
operator|=
operator|new
name|TrieDateField
argument_list|()
expr_stmt|;
name|dt
operator|.
name|init
argument_list|(
name|schema
argument_list|,
name|initMap
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|dt
operator|.
name|hasProperty
argument_list|(
name|FieldType
operator|.
name|OMIT_NORMS
argument_list|)
argument_list|)
expr_stmt|;
name|s
operator|=
operator|new
name|StrField
argument_list|()
expr_stmt|;
name|s
operator|.
name|init
argument_list|(
name|schema
argument_list|,
name|initMap
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|s
operator|.
name|hasProperty
argument_list|(
name|FieldType
operator|.
name|OMIT_NORMS
argument_list|)
argument_list|)
expr_stmt|;
name|ti
operator|=
operator|new
name|TrieIntField
argument_list|()
expr_stmt|;
name|ti
operator|.
name|init
argument_list|(
name|schema
argument_list|,
name|initMap
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ti
operator|.
name|hasProperty
argument_list|(
name|FieldType
operator|.
name|OMIT_NORMS
argument_list|)
argument_list|)
expr_stmt|;
name|tl
operator|=
operator|new
name|TrieLongField
argument_list|()
expr_stmt|;
name|tl
operator|.
name|init
argument_list|(
name|schema
argument_list|,
name|initMap
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tl
operator|.
name|hasProperty
argument_list|(
name|FieldType
operator|.
name|OMIT_NORMS
argument_list|)
argument_list|)
expr_stmt|;
name|tf
operator|=
operator|new
name|TrieFloatField
argument_list|()
expr_stmt|;
name|tf
operator|.
name|init
argument_list|(
name|schema
argument_list|,
name|initMap
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tf
operator|.
name|hasProperty
argument_list|(
name|FieldType
operator|.
name|OMIT_NORMS
argument_list|)
argument_list|)
expr_stmt|;
name|td
operator|=
operator|new
name|TrieDoubleField
argument_list|()
expr_stmt|;
name|td
operator|.
name|init
argument_list|(
name|schema
argument_list|,
name|initMap
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|td
operator|.
name|hasProperty
argument_list|(
name|FieldType
operator|.
name|OMIT_NORMS
argument_list|)
argument_list|)
expr_stmt|;
name|b
operator|=
operator|new
name|BoolField
argument_list|()
expr_stmt|;
name|b
operator|.
name|init
argument_list|(
name|schema
argument_list|,
name|initMap
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|hasProperty
argument_list|(
name|FieldType
operator|.
name|OMIT_NORMS
argument_list|)
argument_list|)
expr_stmt|;
comment|// Non-primitive fields
name|t
operator|=
operator|new
name|TextField
argument_list|()
expr_stmt|;
name|t
operator|.
name|init
argument_list|(
name|schema
argument_list|,
name|initMap
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|t
operator|.
name|hasProperty
argument_list|(
name|FieldType
operator|.
name|OMIT_NORMS
argument_list|)
argument_list|)
expr_stmt|;
name|bin
operator|=
operator|new
name|BinaryField
argument_list|()
expr_stmt|;
name|bin
operator|.
name|init
argument_list|(
name|schema
argument_list|,
name|initMap
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|bin
operator|.
name|hasProperty
argument_list|(
name|FieldType
operator|.
name|OMIT_NORMS
argument_list|)
argument_list|)
expr_stmt|;
comment|// ***********************
comment|// With schema version 1.5
comment|// ***********************
name|schema
operator|=
name|IndexSchemaFactory
operator|.
name|buildIndexSchema
argument_list|(
name|testConfHome
operator|+
literal|"schema15.xml"
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|dt
operator|=
operator|new
name|TrieDateField
argument_list|()
expr_stmt|;
name|dt
operator|.
name|init
argument_list|(
name|schema
argument_list|,
name|initMap
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dt
operator|.
name|hasProperty
argument_list|(
name|FieldType
operator|.
name|OMIT_NORMS
argument_list|)
argument_list|)
expr_stmt|;
name|s
operator|=
operator|new
name|StrField
argument_list|()
expr_stmt|;
name|s
operator|.
name|init
argument_list|(
name|schema
argument_list|,
name|initMap
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|s
operator|.
name|hasProperty
argument_list|(
name|FieldType
operator|.
name|OMIT_NORMS
argument_list|)
argument_list|)
expr_stmt|;
name|ti
operator|=
operator|new
name|TrieIntField
argument_list|()
expr_stmt|;
name|ti
operator|.
name|init
argument_list|(
name|schema
argument_list|,
name|initMap
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ti
operator|.
name|hasProperty
argument_list|(
name|FieldType
operator|.
name|OMIT_NORMS
argument_list|)
argument_list|)
expr_stmt|;
name|tl
operator|=
operator|new
name|TrieLongField
argument_list|()
expr_stmt|;
name|tl
operator|.
name|init
argument_list|(
name|schema
argument_list|,
name|initMap
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tl
operator|.
name|hasProperty
argument_list|(
name|FieldType
operator|.
name|OMIT_NORMS
argument_list|)
argument_list|)
expr_stmt|;
name|tf
operator|=
operator|new
name|TrieFloatField
argument_list|()
expr_stmt|;
name|tf
operator|.
name|init
argument_list|(
name|schema
argument_list|,
name|initMap
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tf
operator|.
name|hasProperty
argument_list|(
name|FieldType
operator|.
name|OMIT_NORMS
argument_list|)
argument_list|)
expr_stmt|;
name|td
operator|=
operator|new
name|TrieDoubleField
argument_list|()
expr_stmt|;
name|td
operator|.
name|init
argument_list|(
name|schema
argument_list|,
name|initMap
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|td
operator|.
name|hasProperty
argument_list|(
name|FieldType
operator|.
name|OMIT_NORMS
argument_list|)
argument_list|)
expr_stmt|;
name|b
operator|=
operator|new
name|BoolField
argument_list|()
expr_stmt|;
name|b
operator|.
name|init
argument_list|(
name|schema
argument_list|,
name|initMap
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|hasProperty
argument_list|(
name|FieldType
operator|.
name|OMIT_NORMS
argument_list|)
argument_list|)
expr_stmt|;
comment|// Non-primitive fields
name|t
operator|=
operator|new
name|TextField
argument_list|()
expr_stmt|;
name|t
operator|.
name|init
argument_list|(
name|schema
argument_list|,
name|initMap
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|t
operator|.
name|hasProperty
argument_list|(
name|FieldType
operator|.
name|OMIT_NORMS
argument_list|)
argument_list|)
expr_stmt|;
name|bin
operator|=
operator|new
name|BinaryField
argument_list|()
expr_stmt|;
name|bin
operator|.
name|init
argument_list|(
name|schema
argument_list|,
name|initMap
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|bin
operator|.
name|hasProperty
argument_list|(
name|FieldType
operator|.
name|OMIT_NORMS
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testTrieDateField
specifier|public
name|void
name|testTrieDateField
parameter_list|()
block|{
name|schema
operator|=
name|IndexSchemaFactory
operator|.
name|buildIndexSchema
argument_list|(
name|testConfHome
operator|+
literal|"schema15.xml"
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|TrieDateField
name|tdt
init|=
operator|new
name|TrieDateField
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"sortMissingLast"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"indexed"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"stored"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"docValues"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"precisionStep"
argument_list|,
literal|"16"
argument_list|)
expr_stmt|;
name|tdt
operator|.
name|setArgs
argument_list|(
name|schema
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdt
operator|.
name|hasProperty
argument_list|(
name|FieldType
operator|.
name|OMIT_NORMS
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdt
operator|.
name|hasProperty
argument_list|(
name|FieldType
operator|.
name|SORT_MISSING_LAST
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdt
operator|.
name|hasProperty
argument_list|(
name|FieldType
operator|.
name|INDEXED
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tdt
operator|.
name|hasProperty
argument_list|(
name|FieldType
operator|.
name|STORED
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tdt
operator|.
name|hasProperty
argument_list|(
name|FieldType
operator|.
name|DOC_VALUES
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|16
argument_list|,
name|tdt
operator|.
name|getPrecisionStep
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
