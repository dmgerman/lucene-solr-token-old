begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package
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
name|junit
operator|.
name|Assert
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
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|JUnitCore
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|Result
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|notification
operator|.
name|Failure
import|;
end_import
begin_comment
comment|/**  * Ensures proper functions of {@link LuceneTestCase#setUp()}  * and {@link LuceneTestCase#tearDown()}.  */
end_comment
begin_class
DECL|class|TestSetupTeardownChaining
specifier|public
class|class
name|TestSetupTeardownChaining
extends|extends
name|WithNestedTests
block|{
DECL|class|NestedSetupChain
specifier|public
specifier|static
class|class
name|NestedSetupChain
extends|extends
name|AbstractNestedTest
block|{
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
comment|// missing call.
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Hello."
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMe
specifier|public
name|void
name|testMe
parameter_list|()
block|{     }
block|}
DECL|class|NestedTeardownChain
specifier|public
specifier|static
class|class
name|NestedTeardownChain
extends|extends
name|AbstractNestedTest
block|{
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
comment|// missing call.
block|}
annotation|@
name|Test
DECL|method|testMe
specifier|public
name|void
name|testMe
parameter_list|()
block|{     }
block|}
DECL|method|TestSetupTeardownChaining
specifier|public
name|TestSetupTeardownChaining
parameter_list|()
block|{
name|super
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify super method calls on {@link LuceneTestCase#setUp()}.    */
annotation|@
name|Test
DECL|method|testSetupChaining
specifier|public
name|void
name|testSetupChaining
parameter_list|()
block|{
name|Result
name|result
init|=
name|JUnitCore
operator|.
name|runClasses
argument_list|(
name|NestedSetupChain
operator|.
name|class
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|getFailureCount
argument_list|()
argument_list|)
expr_stmt|;
name|Failure
name|failure
init|=
name|result
operator|.
name|getFailures
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|failure
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"One of the overrides of setUp does not propagate the call."
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify super method calls on {@link LuceneTestCase#tearDown()}.    */
annotation|@
name|Test
DECL|method|testTeardownChaining
specifier|public
name|void
name|testTeardownChaining
parameter_list|()
block|{
name|Result
name|result
init|=
name|JUnitCore
operator|.
name|runClasses
argument_list|(
name|NestedTeardownChain
operator|.
name|class
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|getFailureCount
argument_list|()
argument_list|)
expr_stmt|;
name|Failure
name|failure
init|=
name|result
operator|.
name|getFailures
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|failure
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"One of the overrides of tearDown does not propagate the call."
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
