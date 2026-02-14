import SwiftUI
import UIKit
import NotesShared

private struct NotesComposeContainer: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        NotesViewControllerKt.makeNotesViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        NotesComposeContainer()
            .ignoresSafeArea()
    }
}
