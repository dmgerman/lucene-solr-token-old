begin_unit
begin_package
DECL|package|org.apache.lucene.sandbox.queries.regex
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|sandbox
operator|.
name|queries
operator|.
name|regex
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
begin_comment
comment|/**  * Testcase for {@link JakartaRegexpCapabilities}  */
end_comment
begin_class
DECL|class|TestJakartaRegexpCapabilities
specifier|public
class|class
name|TestJakartaRegexpCapabilities
extends|extends
name|LuceneTestCase
block|{
DECL|method|testGetPrefix
specifier|public
name|void
name|testGetPrefix
parameter_list|()
block|{
name|JakartaRegexpCapabilities
name|cap
init|=
operator|new
name|JakartaRegexpCapabilities
argument_list|()
decl_stmt|;
name|RegexCapabilities
operator|.
name|RegexMatcher
name|matcher
init|=
name|cap
operator|.
name|compile
argument_list|(
literal|"luc[e]?"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|matcher
operator|.
name|match
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"luce"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"luc"
argument_list|,
name|matcher
operator|.
name|prefix
argument_list|()
argument_list|)
expr_stmt|;
name|matcher
operator|=
name|cap
operator|.
name|compile
argument_list|(
literal|"lucene"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|matcher
operator|.
name|match
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"lucene"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"lucene"
argument_list|,
name|matcher
operator|.
name|prefix
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testShakyPrefix
specifier|public
name|void
name|testShakyPrefix
parameter_list|()
block|{
name|JakartaRegexpCapabilities
name|cap
init|=
operator|new
name|JakartaRegexpCapabilities
argument_list|()
decl_stmt|;
name|RegexCapabilities
operator|.
name|RegexMatcher
name|matcher
init|=
name|cap
operator|.
name|compile
argument_list|(
literal|"(ab|ac)"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|matcher
operator|.
name|match
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"ab"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|matcher
operator|.
name|match
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"ac"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// why is it not a???
name|assertNull
argument_list|(
name|matcher
operator|.
name|prefix
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
