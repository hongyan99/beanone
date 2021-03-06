package org.beanone;

import java.util.ArrayList;
import java.util.List;

import org.beanone.testbeans.TestObjectFactory;
import org.beanone.testbeans.UserDetail;
import org.junit.Assert;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class BeanHistoryTest {
	private static final String BEAN_WITH_THREE_VERSIONS_JSON = "{\"initialState\":{\"position\":1,\"userId\":\"bob.smith\",\"person\":{\"firstName\":\"Bob\",\"lastName\":\"Smith\",\"emailAddresses\":[],\"phones\":[]},\"addresses\":[{\"streetAddress\":\"123 Main St.\",\"city\":\"Seattle\",\"zip\":123}],\"relations\":{\"brother\":{\"firstName\":\"William\",\"lastName\":\"Smith\",\"emailAddresses\":[],\"phones\":[]}}},\"latestState\":{\"position\":1,\"userId\":\"bob.smith\",\"person\":{\"firstName\":\"Bobby\",\"lastName\":\"Smith\",\"emailAddresses\":[],\"phones\":[]},\"addresses\":[{\"streetAddress\":\"123 Main St.\",\"city\":\"Seattle\",\"zip\":123},{\"streetAddress\":\"222 Blue Ave.\",\"city\":\"Master\",\"zip\":111}],\"relations\":{}},\"patches\":[{\"additions\":{},\"deletions\":{},\"updates\":{\"person.firstName\":{\"oldValue\":\"S,Bob\",\"newValue\":\"S,Bobby\"}}},{\"additions\":{\"addresses.1.city\":\"S,Master\",\"addresses.1.streetAddress\":\"S,222 Blue Ave.\",\"addresses.1.zip\":\"I,111\",\"addresses.1%1cty\":\"org.beanone.testbeans.Address\"},\"deletions\":{\"relations.1%1val%1cty\":\"org.beanone.testbeans.Person\",\"relations.1%1val.emailAddresses%2siz\":\"0\",\"relations.1%1key\":\"S,brother\",\"relations.1%1val.phones%2siz\":\"0\",\"relations.1%1val.emailAddresses%1cty\":\"java.util.ArrayList\",\"relations.1%1val.firstName\":\"S,William\",\"relations.1%1val.lastName\":\"S,Smith\",\"relations.1%1val.phones%1cty\":\"java.util.ArrayList\"},\"updates\":{\"addresses%2siz\":{\"oldValue\":\"1\",\"newValue\":\"2\"},\"relations%2siz\":{\"oldValue\":\"1\",\"newValue\":\"0\"}}}]}";
	private final Gson gson = new GsonBuilder().create();

	@Test
	public void testBeanHistory() throws Exception {
		final BeanHistory<UserDetail> bh = TestObjectFactory
		        .createTestBeanHistory();
		Assert.assertNotNull(bh.getInitialState());
		Assert.assertNotNull(bh.getInitialState().getPerson());
		Assert.assertNotNull(bh.getInitialState().getPerson().getFirstName());
		Assert.assertNotNull(bh.getLatestState());
		Assert.assertNotNull(bh.getInitialSnapshot());
		Assert.assertNotNull(bh.getLastestSnapshot());
		Assert.assertEquals(0, bh.getPatches().size());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testBeanHistoryGetPatchesUnmodifiable() throws Exception {
		final BeanHistory<UserDetail> hist = TestObjectFactory
		        .createTestBeanHistoryWithThreeVersions();
		final List<BeanPatch<UserDetail>> patches = hist.getPatches();
		Assert.assertEquals(2, patches.size());
		patches.remove(0);
	}

	@Test
	public void testBeanHistoryInitialStateFinalStatePatches()
	        throws Exception {
		final BeanHistory<UserDetail> bh = TestObjectFactory
		        .createTestBeanHistoryWithThreeVersions();
		final BeanHistory<UserDetail> bh1 = new BeanHistory<>(
		        bh.getInitialState(), bh.getLatestState(), bh.getPatches());
		Assert.assertEquals(BEAN_WITH_THREE_VERSIONS_JSON,
		        this.gson.toJson(bh1));
		final BeanHistory<UserDetail> bh2 = new BeanHistory<>(null,
		        bh.getLatestState(), bh.getPatches());
		Assert.assertEquals(BEAN_WITH_THREE_VERSIONS_JSON,
		        this.gson.toJson(bh2));
		final BeanHistory<UserDetail> bh3 = new BeanHistory<>(
		        bh.getInitialState(), null, bh.getPatches());
		Assert.assertEquals(BEAN_WITH_THREE_VERSIONS_JSON,
		        this.gson.toJson(bh3));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBeanHistoryIntialStateFinalStateBothNull()
	        throws Exception {
		new BeanHistory<>(null, null, new ArrayList<>());
	}

	@Test
	public void testBeanHistoryPatchesNull() throws Exception {
		final BeanHistory<UserDetail> bh = TestObjectFactory
		        .createTestBeanHistory();
		final BeanHistory<UserDetail> bh1 = new BeanHistory<>(
		        bh.getInitialState(), null, null);
		Assert.assertNotNull(bh1.getLatestState());
		Assert.assertEquals(this.gson.toJson(bh1.getInitialState()),
		        this.gson.toJson(bh1.getLatestState()));
	}

	@Test
	public void testBeanHistoryWithPatchesSerializedAsJson() throws Exception {
		final BeanHistory<UserDetail> beanHistory = TestObjectFactory
		        .createTestBeanHistoryWithThreeVersions();
		Assert.assertEquals(BEAN_WITH_THREE_VERSIONS_JSON,
		        this.gson.toJson(beanHistory));
	}

	@Test
	public void testCreatePatchBeanUpdater() throws Exception {
		final BeanHistory<UserDetail> bh = TestObjectFactory
		        .createTestBeanHistory();
		bh.createPatch(bean -> bean.getPerson().setFirstName("Bobby"));

		Assert.assertEquals(1, bh.getPatches().size());
		Assert.assertEquals("S,Bobby", bh.getPatches().get(0).getUpdates()
		        .get("person.firstName").getNewValue());
	}

	@Test
	public void testCreatePatchBeanUpdaterWithNoChanges() throws Exception {
		final BeanHistory<UserDetail> bh = TestObjectFactory
		        .createTestBeanHistory();
		Assert.assertNull(bh.createPatch(bean -> bean.getPerson()));
	}

	@Test
	public void testCreatePatchT() throws Exception {
		final UserDetail userDetail = TestObjectFactory.createTestUserDetail();
		final BeanHistory<UserDetail> bh = new BeanHistory<UserDetail>(
		        userDetail);
		userDetail.getPerson().setFirstName("Bobby");
		bh.createPatch(userDetail);

		Assert.assertEquals(1, bh.getPatches().size());
		Assert.assertEquals("S,Bobby", bh.getPatches().get(0).getUpdates()
		        .get("person.firstName").getNewValue());
	}

	@Test
	public void testCreatePatchTWithNoChanges() throws Exception {
		final UserDetail userDetail = TestObjectFactory.createTestUserDetail();
		final BeanHistory<UserDetail> bh = new BeanHistory<UserDetail>(
		        userDetail);
		Assert.assertNull(bh.createPatch(userDetail));
	}
}
