begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.internal.csv.writer
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|internal
operator|.
name|csv
operator|.
name|writer
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import
begin_comment
comment|/**  * Testcase for the CSVConfig  */
end_comment
begin_class
DECL|class|CSVConfigTest
specifier|public
class|class
name|CSVConfigTest
extends|extends
name|TestCase
block|{
DECL|method|testFixedWith
specifier|public
name|void
name|testFixedWith
parameter_list|()
block|{
name|CSVConfig
name|config
init|=
operator|new
name|CSVConfig
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|config
operator|.
name|isFixedWidth
argument_list|()
argument_list|)
expr_stmt|;
name|config
operator|.
name|setFixedWidth
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|config
operator|.
name|isFixedWidth
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testFields
specifier|public
name|void
name|testFields
parameter_list|()
block|{
name|CSVConfig
name|config
init|=
operator|new
name|CSVConfig
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|config
operator|.
name|getFields
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|config
operator|.
name|setFields
argument_list|(
operator|(
name|CSVField
index|[]
operator|)
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|config
operator|.
name|getFields
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|config
operator|.
name|setFields
argument_list|(
operator|(
name|Collection
operator|)
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|config
operator|.
name|getFields
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|CSVField
name|field
init|=
operator|new
name|CSVField
argument_list|()
decl_stmt|;
name|field
operator|.
name|setName
argument_list|(
literal|"field1"
argument_list|)
expr_stmt|;
name|config
operator|.
name|addField
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|field
argument_list|,
name|config
operator|.
name|getFields
argument_list|()
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|config
operator|.
name|getField
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|config
operator|.
name|getField
argument_list|(
literal|"field11"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|field
argument_list|,
name|config
operator|.
name|getField
argument_list|(
literal|"field1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testFill
specifier|public
name|void
name|testFill
parameter_list|()
block|{
name|CSVConfig
name|config
init|=
operator|new
name|CSVConfig
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|CSVConfig
operator|.
name|FILLNONE
argument_list|,
name|config
operator|.
name|getFill
argument_list|()
argument_list|)
expr_stmt|;
name|config
operator|.
name|setFill
argument_list|(
name|CSVConfig
operator|.
name|FILLLEFT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|CSVConfig
operator|.
name|FILLLEFT
argument_list|,
name|config
operator|.
name|getFill
argument_list|()
argument_list|)
expr_stmt|;
name|config
operator|.
name|setFill
argument_list|(
name|CSVConfig
operator|.
name|FILLRIGHT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|CSVConfig
operator|.
name|FILLRIGHT
argument_list|,
name|config
operator|.
name|getFill
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|' '
argument_list|,
name|config
operator|.
name|getFillChar
argument_list|()
argument_list|)
expr_stmt|;
name|config
operator|.
name|setFillChar
argument_list|(
literal|'m'
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|'m'
argument_list|,
name|config
operator|.
name|getFillChar
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testDelimiter
specifier|public
name|void
name|testDelimiter
parameter_list|()
block|{
name|CSVConfig
name|config
init|=
operator|new
name|CSVConfig
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|','
argument_list|,
name|config
operator|.
name|getDelimiter
argument_list|()
argument_list|)
expr_stmt|;
name|config
operator|.
name|setDelimiter
argument_list|(
literal|';'
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|';'
argument_list|,
name|config
operator|.
name|getDelimiter
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|config
operator|.
name|isDelimiterIgnored
argument_list|()
argument_list|)
expr_stmt|;
name|config
operator|.
name|setIgnoreDelimiter
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|config
operator|.
name|isDelimiterIgnored
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testValueDelimiter
specifier|public
name|void
name|testValueDelimiter
parameter_list|()
block|{
name|CSVConfig
name|config
init|=
operator|new
name|CSVConfig
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|'"'
argument_list|,
name|config
operator|.
name|getValueDelimiter
argument_list|()
argument_list|)
expr_stmt|;
name|config
operator|.
name|setValueDelimiter
argument_list|(
literal|'m'
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|'m'
argument_list|,
name|config
operator|.
name|getValueDelimiter
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|config
operator|.
name|isValueDelimiterIgnored
argument_list|()
argument_list|)
expr_stmt|;
name|config
operator|.
name|setIgnoreValueDelimiter
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|config
operator|.
name|isValueDelimiterIgnored
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testFieldHeader
specifier|public
name|void
name|testFieldHeader
parameter_list|()
block|{
name|CSVConfig
name|config
init|=
operator|new
name|CSVConfig
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|config
operator|.
name|isFieldHeader
argument_list|()
argument_list|)
expr_stmt|;
name|config
operator|.
name|setFieldHeader
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|config
operator|.
name|isFieldHeader
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testTrimEnd
specifier|public
name|void
name|testTrimEnd
parameter_list|()
block|{
name|CSVConfig
name|config
init|=
operator|new
name|CSVConfig
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|config
operator|.
name|isEndTrimmed
argument_list|()
argument_list|)
expr_stmt|;
name|config
operator|.
name|setEndTrimmed
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|config
operator|.
name|isEndTrimmed
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
