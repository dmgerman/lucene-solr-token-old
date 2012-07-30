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
name|core
operator|.
name|AbstractBadConfigTestBase
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import
begin_class
DECL|class|BadIndexSchemaTest
specifier|public
class|class
name|BadIndexSchemaTest
extends|extends
name|AbstractBadConfigTestBase
block|{
DECL|method|doTest
specifier|private
name|void
name|doTest
parameter_list|(
specifier|final
name|String
name|schema
parameter_list|,
specifier|final
name|String
name|errString
parameter_list|)
throws|throws
name|Exception
block|{
name|assertConfigs
argument_list|(
literal|"solrconfig.xml"
argument_list|,
name|schema
argument_list|,
name|errString
argument_list|)
expr_stmt|;
block|}
DECL|method|testSevereErrorsForInvalidFieldOptions
specifier|public
name|void
name|testSevereErrorsForInvalidFieldOptions
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|"bad-schema-not-indexed-but-norms.xml"
argument_list|,
literal|"bad_field"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
literal|"bad-schema-not-indexed-but-tf.xml"
argument_list|,
literal|"bad_field"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
literal|"bad-schema-not-indexed-but-pos.xml"
argument_list|,
literal|"bad_field"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
literal|"bad-schema-omit-tf-but-not-pos.xml"
argument_list|,
literal|"bad_field"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSevereErrorsForDuplicateFields
specifier|public
name|void
name|testSevereErrorsForDuplicateFields
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|"bad-schema-dup-field.xml"
argument_list|,
literal|"fAgain"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSevereErrorsForDuplicateDynamicField
specifier|public
name|void
name|testSevereErrorsForDuplicateDynamicField
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|"bad-schema-dup-dynamicField.xml"
argument_list|,
literal|"_twice"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSevereErrorsForDuplicateFieldType
specifier|public
name|void
name|testSevereErrorsForDuplicateFieldType
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|"bad-schema-dup-fieldType.xml"
argument_list|,
literal|"ftAgain"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSevereErrorsForUnexpectedAnalyzer
specifier|public
name|void
name|testSevereErrorsForUnexpectedAnalyzer
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|"bad-schema-nontext-analyzer.xml"
argument_list|,
literal|"StrField (bad_type)"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
literal|"bad-schema-analyzer-class-and-nested.xml"
argument_list|,
literal|"bad_type"
argument_list|)
expr_stmt|;
block|}
DECL|method|testBadExternalFileField
specifier|public
name|void
name|testBadExternalFileField
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|"bad-schema-external-filefield.xml"
argument_list|,
literal|"Only float and pfloat"
argument_list|)
expr_stmt|;
block|}
DECL|method|testUniqueKeyRules
specifier|public
name|void
name|testUniqueKeyRules
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|"bad-schema-uniquekey-is-copyfield-dest.xml"
argument_list|,
literal|"can not be the dest of a copyField"
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
literal|"bad-schema-uniquekey-uses-default.xml"
argument_list|,
literal|"can not be configured with a default value"
argument_list|)
expr_stmt|;
block|}
DECL|method|testPerFieldtypeSimButNoSchemaSimFactory
specifier|public
name|void
name|testPerFieldtypeSimButNoSchemaSimFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|"bad-schema-sim-global-vs-ft-mismatch.xml"
argument_list|,
literal|"global similarity does not support it"
argument_list|)
expr_stmt|;
block|}
DECL|method|testPerFieldtypePostingsFormatButNoSchemaCodecFactory
specifier|public
name|void
name|testPerFieldtypePostingsFormatButNoSchemaCodecFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
literal|"bad-schema-codec-global-vs-ft-mismatch.xml"
argument_list|,
literal|"codec does not support"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
