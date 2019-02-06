/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.sunflower

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.samples.apps.sunflower.adapters.GardenPlantingAdapter
import com.google.samples.apps.sunflower.databinding.FragmentGardenBinding
import com.google.samples.apps.sunflower.utilities.InjectorUtils
import com.google.samples.apps.sunflower.viewmodels.GardenPlantingListViewModel

class GardenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentGardenBinding.inflate(inflater, container, false)
        val adapter = GardenPlantingAdapter()
        // Set RecyclerView adapter
        binding.gardenList.adapter = adapter
        /**
         * TODO 1: If this is a long lasting event, i.e. repo is pulling data
         * from network, then is it still going to update the update adapter
         * data before view is created?
         * Or is it going to update it whenever it finishes and the user will
         * look at whatever is available(worst an empty screen), until result
         * is ready?
         * - Emre
         */
        /**
         * ANSWER: Yes, it will show whatever is available (hopefully handled)
         * until the result is ready. To handle such situations properly see
         * Adendum: Exposing Network Status in the link below:
         * https://developer.android.com/jetpack/docs/guide#show-in-progress-operations
         * - Brian
         */
        subscribeUi(adapter, binding)
        return binding.root
    }

    private fun subscribeUi(adapter: GardenPlantingAdapter, binding: FragmentGardenBinding) {
        /**
         * TODO 2: Why use a custom ViewModelFactory? What is the advantage?
         * - Emre
         */
        /**
         * ANSWER: Custom ViewModelFactory class allows custom constructors,
         * such as the ones that gets a repo instance as an argument, whcih
         * can be seen in every ViewModelFactory class in this app.
         * - Brian
         */
        val factory = InjectorUtils.provideGardenPlantingListViewModelFactory(requireContext())
        val viewModel = ViewModelProviders.of(this, factory)
                .get(GardenPlantingListViewModel::class.java)

        /**
         * TODO 3: Why not call 'this' instead of viewLifeCycleOwner as context?
         * - Emre
         */
        viewModel.gardenPlantings.observe(viewLifecycleOwner, Observer { plantings ->
            // Custom DataBinding Adapter (see BindingAdapters.kt) sets hasPlantings
            // to display RecyclerView or Empty TextView
            binding.hasPlantings = (plantings != null && plantings.isNotEmpty())
        })

        viewModel.plantAndGardenPlantings.observe(viewLifecycleOwner, Observer { result ->
            if (result != null && result.isNotEmpty())
                // Update ListAdapter data
                adapter.submitList(result)
        })
    }
}
