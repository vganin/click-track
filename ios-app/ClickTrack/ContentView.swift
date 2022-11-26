//
//  ContentView.swift
//  ClickTrack
//
//  Created by Vsevolod Ganin on 25.11.2022.
//

import SwiftUI
import ClickTrackMultiplatform

struct ContentView: View {
    var body: some View {
        Text(Test.shared.test())
            .padding()
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
