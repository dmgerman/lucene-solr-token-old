begin_unit
begin_package
DECL|package|org.apache.solr.common.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
begin_class
DECL|class|TestDateUtil
specifier|public
class|class
name|TestDateUtil
extends|extends
name|LuceneTestCase
block|{
DECL|method|testCurrentTime
specifier|public
name|void
name|testCurrentTime
parameter_list|()
throws|throws
name|ParseException
block|{
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|assertParsedDate
argument_list|(
name|now
argument_list|,
operator|new
name|Date
argument_list|(
name|now
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
literal|1000L
argument_list|)
expr_stmt|;
block|}
DECL|method|testParseDate
specifier|public
name|void
name|testParseDate
parameter_list|()
throws|throws
name|ParseException
block|{
name|assertParsedDate
argument_list|(
literal|1226583351000L
argument_list|,
literal|"Thu Nov 13 04:35:51 AKST 2008"
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
block|}
DECL|method|assertParsedDate
specifier|private
specifier|static
name|void
name|assertParsedDate
parameter_list|(
name|long
name|ts
parameter_list|,
name|String
name|dateStr
parameter_list|,
name|long
name|epsilon
parameter_list|)
throws|throws
name|ParseException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|DateUtil
operator|.
name|parseDate
argument_list|(
name|dateStr
argument_list|)
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Incorrect parsed timestamp"
argument_list|,
name|Math
operator|.
name|abs
argument_list|(
name|ts
operator|-
name|DateUtil
operator|.
name|parseDate
argument_list|(
name|dateStr
argument_list|)
operator|.
name|getTime
argument_list|()
argument_list|)
operator|<=
name|epsilon
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
