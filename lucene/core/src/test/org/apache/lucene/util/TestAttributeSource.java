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
name|analysis
operator|.
name|Token
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
name|TokenStream
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
name|tokenattributes
operator|.
name|*
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
name|Iterator
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
begin_class
DECL|class|TestAttributeSource
specifier|public
class|class
name|TestAttributeSource
extends|extends
name|LuceneTestCase
block|{
DECL|method|testCaptureState
specifier|public
name|void
name|testCaptureState
parameter_list|()
block|{
comment|// init a first instance
name|AttributeSource
name|src
init|=
operator|new
name|AttributeSource
argument_list|()
decl_stmt|;
name|CharTermAttribute
name|termAtt
init|=
name|src
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|TypeAttribute
name|typeAtt
init|=
name|src
operator|.
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|termAtt
operator|.
name|append
argument_list|(
literal|"TestTerm"
argument_list|)
expr_stmt|;
name|typeAtt
operator|.
name|setType
argument_list|(
literal|"TestType"
argument_list|)
expr_stmt|;
specifier|final
name|int
name|hashCode
init|=
name|src
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|AttributeSource
operator|.
name|State
name|state
init|=
name|src
operator|.
name|captureState
argument_list|()
decl_stmt|;
comment|// modify the attributes
name|termAtt
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
literal|"AnotherTestTerm"
argument_list|)
expr_stmt|;
name|typeAtt
operator|.
name|setType
argument_list|(
literal|"AnotherTestType"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Hash code should be different"
argument_list|,
name|hashCode
operator|!=
name|src
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|src
operator|.
name|restoreState
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"TestTerm"
argument_list|,
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"TestType"
argument_list|,
name|typeAtt
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Hash code should be equal after restore"
argument_list|,
name|hashCode
argument_list|,
name|src
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
comment|// restore into an exact configured copy
name|AttributeSource
name|copy
init|=
operator|new
name|AttributeSource
argument_list|()
decl_stmt|;
name|copy
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|copy
operator|.
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|copy
operator|.
name|restoreState
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Both AttributeSources should have same hashCode after restore"
argument_list|,
name|src
operator|.
name|hashCode
argument_list|()
argument_list|,
name|copy
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Both AttributeSources should be equal after restore"
argument_list|,
name|src
argument_list|,
name|copy
argument_list|)
expr_stmt|;
comment|// init a second instance (with attributes in different order and one additional attribute)
name|AttributeSource
name|src2
init|=
operator|new
name|AttributeSource
argument_list|()
decl_stmt|;
name|typeAtt
operator|=
name|src2
operator|.
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|FlagsAttribute
name|flagsAtt
init|=
name|src2
operator|.
name|addAttribute
argument_list|(
name|FlagsAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|termAtt
operator|=
name|src2
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|flagsAtt
operator|.
name|setFlags
argument_list|(
literal|12345
argument_list|)
expr_stmt|;
name|src2
operator|.
name|restoreState
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"TestTerm"
argument_list|,
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"TestType"
argument_list|,
name|typeAtt
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"FlagsAttribute should not be touched"
argument_list|,
literal|12345
argument_list|,
name|flagsAtt
operator|.
name|getFlags
argument_list|()
argument_list|)
expr_stmt|;
comment|// init a third instance missing one Attribute
name|AttributeSource
name|src3
init|=
operator|new
name|AttributeSource
argument_list|()
decl_stmt|;
name|termAtt
operator|=
name|src3
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// The third instance is missing the TypeAttribute, so restoreState() should throw IllegalArgumentException
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|src3
operator|.
name|restoreState
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testCloneAttributes
specifier|public
name|void
name|testCloneAttributes
parameter_list|()
block|{
specifier|final
name|AttributeSource
name|src
init|=
operator|new
name|AttributeSource
argument_list|()
decl_stmt|;
specifier|final
name|FlagsAttribute
name|flagsAtt
init|=
name|src
operator|.
name|addAttribute
argument_list|(
name|FlagsAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|TypeAttribute
name|typeAtt
init|=
name|src
operator|.
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|flagsAtt
operator|.
name|setFlags
argument_list|(
literal|1234
argument_list|)
expr_stmt|;
name|typeAtt
operator|.
name|setType
argument_list|(
literal|"TestType"
argument_list|)
expr_stmt|;
specifier|final
name|AttributeSource
name|clone
init|=
name|src
operator|.
name|cloneAttributes
argument_list|()
decl_stmt|;
specifier|final
name|Iterator
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
argument_list|>
name|it
init|=
name|clone
operator|.
name|getAttributeClassesIterator
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"FlagsAttribute must be the first attribute"
argument_list|,
name|FlagsAttribute
operator|.
name|class
argument_list|,
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"TypeAttribute must be the second attribute"
argument_list|,
name|TypeAttribute
operator|.
name|class
argument_list|,
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"No more attributes"
argument_list|,
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|FlagsAttribute
name|flagsAtt2
init|=
name|clone
operator|.
name|getAttribute
argument_list|(
name|FlagsAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|flagsAtt2
argument_list|)
expr_stmt|;
specifier|final
name|TypeAttribute
name|typeAtt2
init|=
name|clone
operator|.
name|getAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|typeAtt2
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
literal|"FlagsAttribute of original and clone must be different instances"
argument_list|,
name|flagsAtt2
argument_list|,
name|flagsAtt
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
literal|"TypeAttribute of original and clone must be different instances"
argument_list|,
name|typeAtt2
argument_list|,
name|typeAtt
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"FlagsAttribute of original and clone must be equal"
argument_list|,
name|flagsAtt2
argument_list|,
name|flagsAtt
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"TypeAttribute of original and clone must be equal"
argument_list|,
name|typeAtt2
argument_list|,
name|typeAtt
argument_list|)
expr_stmt|;
comment|// test copy back
name|flagsAtt2
operator|.
name|setFlags
argument_list|(
literal|4711
argument_list|)
expr_stmt|;
name|typeAtt2
operator|.
name|setType
argument_list|(
literal|"OtherType"
argument_list|)
expr_stmt|;
name|clone
operator|.
name|copyTo
argument_list|(
name|src
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"FlagsAttribute of original must now contain updated term"
argument_list|,
literal|4711
argument_list|,
name|flagsAtt
operator|.
name|getFlags
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"TypeAttribute of original must now contain updated type"
argument_list|,
literal|"OtherType"
argument_list|,
name|typeAtt
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
comment|// verify again:
name|assertNotSame
argument_list|(
literal|"FlagsAttribute of original and clone must be different instances"
argument_list|,
name|flagsAtt2
argument_list|,
name|flagsAtt
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
literal|"TypeAttribute of original and clone must be different instances"
argument_list|,
name|typeAtt2
argument_list|,
name|typeAtt
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"FlagsAttribute of original and clone must be equal"
argument_list|,
name|flagsAtt2
argument_list|,
name|flagsAtt
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"TypeAttribute of original and clone must be equal"
argument_list|,
name|typeAtt2
argument_list|,
name|typeAtt
argument_list|)
expr_stmt|;
block|}
DECL|method|testDefaultAttributeFactory
specifier|public
name|void
name|testDefaultAttributeFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|AttributeSource
name|src
init|=
operator|new
name|AttributeSource
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"CharTermAttribute is not implemented by CharTermAttributeImpl"
argument_list|,
name|src
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
operator|instanceof
name|CharTermAttributeImpl
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"OffsetAttribute is not implemented by OffsetAttributeImpl"
argument_list|,
name|src
operator|.
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
operator|instanceof
name|OffsetAttributeImpl
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"FlagsAttribute is not implemented by FlagsAttributeImpl"
argument_list|,
name|src
operator|.
name|addAttribute
argument_list|(
name|FlagsAttribute
operator|.
name|class
argument_list|)
operator|instanceof
name|FlagsAttributeImpl
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"PayloadAttribute is not implemented by PayloadAttributeImpl"
argument_list|,
name|src
operator|.
name|addAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
operator|instanceof
name|PayloadAttributeImpl
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"PositionIncrementAttribute is not implemented by PositionIncrementAttributeImpl"
argument_list|,
name|src
operator|.
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
operator|instanceof
name|PositionIncrementAttributeImpl
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"TypeAttribute is not implemented by TypeAttributeImpl"
argument_list|,
name|src
operator|.
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
operator|instanceof
name|TypeAttributeImpl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
DECL|method|testInvalidArguments
specifier|public
name|void
name|testInvalidArguments
parameter_list|()
throws|throws
name|Exception
block|{
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|AttributeSource
name|src
init|=
operator|new
name|AttributeSource
argument_list|()
decl_stmt|;
name|src
operator|.
name|addAttribute
argument_list|(
name|Token
operator|.
name|class
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should throw IllegalArgumentException"
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|AttributeSource
name|src
init|=
operator|new
name|AttributeSource
argument_list|(
name|Token
operator|.
name|TOKEN_ATTRIBUTE_FACTORY
argument_list|)
decl_stmt|;
name|src
operator|.
name|addAttribute
argument_list|(
name|Token
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|AttributeSource
name|src
init|=
operator|new
name|AttributeSource
argument_list|()
decl_stmt|;
comment|// break this by unsafe cast
name|src
operator|.
name|addAttribute
argument_list|(
operator|(
name|Class
operator|)
name|Iterator
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testLUCENE_3042
specifier|public
name|void
name|testLUCENE_3042
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AttributeSource
name|src1
init|=
operator|new
name|AttributeSource
argument_list|()
decl_stmt|;
name|src1
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
operator|.
name|append
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|int
name|hash1
init|=
name|src1
operator|.
name|hashCode
argument_list|()
decl_stmt|;
comment|// this triggers a cached state
specifier|final
name|AttributeSource
name|src2
init|=
operator|new
name|AttributeSource
argument_list|(
name|src1
argument_list|)
decl_stmt|;
name|src2
operator|.
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
operator|.
name|setType
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"The hashCode is identical, so the captured state was preserved."
argument_list|,
name|hash1
operator|!=
name|src1
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|src2
operator|.
name|hashCode
argument_list|()
argument_list|,
name|src1
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testClonePayloadAttribute
specifier|public
name|void
name|testClonePayloadAttribute
parameter_list|()
throws|throws
name|Exception
block|{
comment|// LUCENE-6055: verify that PayloadAttribute.clone() does deep cloning.
name|PayloadAttributeImpl
name|src
init|=
operator|new
name|PayloadAttributeImpl
argument_list|(
operator|new
name|BytesRef
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
argument_list|)
decl_stmt|;
comment|// test clone()
name|PayloadAttributeImpl
name|clone
init|=
name|src
operator|.
name|clone
argument_list|()
decl_stmt|;
name|clone
operator|.
name|getPayload
argument_list|()
operator|.
name|bytes
index|[
literal|0
index|]
operator|=
literal|10
expr_stmt|;
comment|// modify one byte, srcBytes shouldn't change
name|assertEquals
argument_list|(
literal|"clone() wasn't deep"
argument_list|,
literal|1
argument_list|,
name|src
operator|.
name|getPayload
argument_list|()
operator|.
name|bytes
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
comment|// test copyTo()
name|clone
operator|=
operator|new
name|PayloadAttributeImpl
argument_list|()
expr_stmt|;
name|src
operator|.
name|copyTo
argument_list|(
name|clone
argument_list|)
expr_stmt|;
name|clone
operator|.
name|getPayload
argument_list|()
operator|.
name|bytes
index|[
literal|0
index|]
operator|=
literal|10
expr_stmt|;
comment|// modify one byte, srcBytes shouldn't change
name|assertEquals
argument_list|(
literal|"clone() wasn't deep"
argument_list|,
literal|1
argument_list|,
name|src
operator|.
name|getPayload
argument_list|()
operator|.
name|bytes
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|testRemoveAllAttributes
specifier|public
name|void
name|testRemoveAllAttributes
parameter_list|()
block|{
name|List
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
argument_list|>
name|attrClasses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|attrClasses
operator|.
name|add
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|attrClasses
operator|.
name|add
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|attrClasses
operator|.
name|add
argument_list|(
name|FlagsAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|attrClasses
operator|.
name|add
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|attrClasses
operator|.
name|add
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|attrClasses
operator|.
name|add
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// Add attributes with the default factory, then try to remove all of them
name|AttributeSource
name|defaultFactoryAttributeSource
init|=
operator|new
name|AttributeSource
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|defaultFactoryAttributeSource
operator|.
name|hasAttributes
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
name|attrClass
range|:
name|attrClasses
control|)
block|{
name|defaultFactoryAttributeSource
operator|.
name|addAttribute
argument_list|(
name|attrClass
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Missing added attribute "
operator|+
name|attrClass
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|defaultFactoryAttributeSource
operator|.
name|hasAttribute
argument_list|(
name|attrClass
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|defaultFactoryAttributeSource
operator|.
name|removeAllAttributes
argument_list|()
expr_stmt|;
for|for
control|(
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
name|attrClass
range|:
name|attrClasses
control|)
block|{
name|assertFalse
argument_list|(
literal|"Didn't remove attribute "
operator|+
name|attrClass
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|defaultFactoryAttributeSource
operator|.
name|hasAttribute
argument_list|(
name|attrClass
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|defaultFactoryAttributeSource
operator|.
name|hasAttributes
argument_list|()
argument_list|)
expr_stmt|;
comment|// Add attributes with the packed implementations factory, then try to remove all of them
name|AttributeSource
name|packedImplsAttributeSource
init|=
operator|new
name|AttributeSource
argument_list|(
name|TokenStream
operator|.
name|DEFAULT_TOKEN_ATTRIBUTE_FACTORY
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|packedImplsAttributeSource
operator|.
name|hasAttributes
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
name|attrClass
range|:
name|attrClasses
control|)
block|{
name|packedImplsAttributeSource
operator|.
name|addAttribute
argument_list|(
name|attrClass
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Missing added attribute "
operator|+
name|attrClass
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|packedImplsAttributeSource
operator|.
name|hasAttribute
argument_list|(
name|attrClass
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|packedImplsAttributeSource
operator|.
name|removeAllAttributes
argument_list|()
expr_stmt|;
for|for
control|(
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
name|attrClass
range|:
name|attrClasses
control|)
block|{
name|assertFalse
argument_list|(
literal|"Didn't remove attribute "
operator|+
name|attrClass
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|packedImplsAttributeSource
operator|.
name|hasAttribute
argument_list|(
name|attrClass
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|packedImplsAttributeSource
operator|.
name|hasAttributes
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
